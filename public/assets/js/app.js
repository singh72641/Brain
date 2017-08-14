var oa = angular.module('oa', [
  'ngAnimate',
  'ngWebSocket'
]);

oa.factory('Messages', function($websocket) {
  var ws = $websocket("ws://localhost:8080/brain/");
  var collection = [];

  ws.onError(function(event) {
    console.log('connection Error', event);
  });

  ws.onClose(function(event) {
    console.log('connection closed', event);
  });

  ws.onOpen(function() {
    console.log('connection open');
    ws.send('START');
  });
  // setTimeout(function() {
  //   ws.close();
  // }, 500)

  return {
    collection: collection,
    status: function() {
      return ws.readyState;
    },
    receiver: function(callback){
      ws.onMessage(function(event) {
        //console.log('message: ', event);
        var res;
        try {
          res = JSON.parse(event.data);
        } catch(e) {
          res = {'username': 'anonymous', 'message': event.data};
        }

        collection.push({
          username: res.username,
          content: res.message,
          timeStamp: event.timeStamp
        });

        callback(res);
      });
    },
    send: function(message) {
      if (angular.isString(message)) {
        ws.send(message);
      }
      else if (angular.isObject(message)) {
        ws.send(JSON.stringify(message));
      }
    }

  };
});


oa.directive('brain', function ($parse) {
     //explicitly creating a directive definition variable
     //this may look verbose but is good for clarification purposes
     //in real life you'd want to simply return the object {...}
     var directiveDefinitionObject = {
     //We restrict its use to an element
     //as usually  <bars-chart> is semantically
     //more understandable
     restrict: 'E',
     //this is important,
     //we don't want to overwrite our directive declaration
     //in the HTML mark-up
     replace: false,
     //our data source would be an array
     //passed thru chart-data attribute

     scope: {data: '=brainData', height: '=height', width: '=width'},

     link: function (scope, element, attrs) {

       var process = function () {
         var chart = d3.select(element[0]);
         chart.html("");
         var svg = chart.append("svg")
                        .attr("height",scope.height+"px")
                        .attr("width", scope.width+"px");
         var color = d3.scaleOrdinal(d3.schemeCategory20);

         var simulation = d3.forceSimulation()
           .force("link", d3.forceLink().id(function(d) {
             return d.id;
           }).distance(80))
           .force("charge", d3.forceManyBody(-250))
           .force("center", d3.forceCenter(scope.width / 2, scope.height / 2));

         var link = svg.append("g")
           .attr("class", "links")
           .selectAll("line")
           .data(scope.data.links)
           .enter().append("line")
           .attr("id", function(d){return d.key;})
           .attr("stroke", function(d) {
              if (d.output != 0) return '#db3327';
              else return '#999' // '#03a310';
           })
           .attr("stroke-width", 2);

         var node = svg.append("g")
           .attr("class", "nodes")
           .selectAll("circle")
           .data(scope.data.nodes)
           .enter().append("circle")
           .attr("r", 10)
           .attr("fill", function(d) {
             return color(d.group);
           })
           .call(d3.drag()
             .on("start", dragstarted)
             .on("drag", dragged)
             .on("end", dragended));
         node.append("title")
           .text(function(d) {
             return d.label + "(" + d.output + ")";
           });
          link.append("title")
             .text(function(d) {
               return d.weight;
             });

           simulation
             .nodes(scope.data.nodes)
             .on("tick", ticked);

           simulation.force("link")
               .links(scope.data.links);

           function ticked() {
             link
               .attr("x1", function(d) {
                 return d.source.x;
               })
               .attr("y1", function(d) {
                 return d.source.y;
               })
               .attr("x2", function(d) {
                 return d.target.x;
               })
               .attr("y2", function(d) {
                 return d.target.y;
               });

             node
               .attr("cx", function(d) {
                 return d.x;
               })
               .attr("cy", function(d) {
                 return d.y;
               });
           }
           function dragstarted(d) {
             if (!d3.event.active) simulation.alphaTarget(0.3).restart();
             d.fx = d.x;
             d.fy = d.y;
           }

           function dragged(d) {
             d.fx = d3.event.x;
             d.fy = d3.event.y;
           }

           function dragended(d) {
             if (!d3.event.active) simulation.alphaTarget(0);
             d.fx = null;
             d.fy = null;
           }
         }

         process();
         scope.$watch("data", function (loading) {
                         process();
                     });
       }
  };
  return directiveDefinitionObject;
});

oa.controller('BrainController', ['$scope', '$compile', 'Messages', function($scope, $compile, Messages) {
  $scope.username = 'anonymous';

  $scope.Messages = Messages;

  $scope.template = "start.html";
  $scope.bdata = [];

  $scope.handler = function(msg){
    console.log("Controller Got Message");
    var data = msg.message;
    console.log(msg);
    if( msg.action == "LOAD"){
      $scope.template = "assets/"+msg.template;
      $scope.bdata = msg.data;
    }
    else if( msg.action == "NEXT"){
      $scope.bdata = msg.data;
    }
  };


  $scope.submit = function(new_message) {
    if (!new_message) { return; }
    Messages.send({
      username: $scope.username,
      message: new_message
    });
    $scope.new_message = '';
  };
  Messages.receiver($scope.handler);


}]);
