package com.punwire.brain;

import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrRelation;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.factories.clause.WHERE;
import iot.jcypher.query.result.JcError;
import iot.jcypher.query.values.JcNode;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;

import javax.json.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by admin on 7/16/2017.
 */
public class Brain {
    public Map<Integer,List<Node>> layers = new HashMap<>();
    public Map<String,Node> nodes = new HashMap<>();

    int numInputs;
    int numOutputs;
    int numHidden;
    int nodeId=0;
    int seq = 0;
    Driver driver=null;
    IDBAccess remote;
    Graph g;
    int currentLayer=addLayer();
    Random rand = new Random();
    public Brain(int i, int o, int h)
    {
        this.numInputs = i;
        this.numOutputs = o;
        this.numHidden = h;
    }

    public void init()
    {

    }

    public Node addNode(String type){
        return addNode(nodeId++,type,currentLayer,null);
    }

    public Node addNode(int id, String type, int layer, IActivation activation){
        Node n=null;
        if( type.equals("NI")) {
            n = new Neuron(id, NodeType.NI, layer);
        }
        else if( type.equals("NO")) {
            n = new Neuron(id, NodeType.NO,layer);
        }
        else if( type.equals("NH")) {
            n = new Neuron(id, NodeType.NH,layer);
        }
        else if( type.equals("NP")) {
            n = new Neuron(id, NodeType.NP,layer);
        }
        else if( type.equals("NN")) {
            n = new Neuron(id, NodeType.NN,layer);
        }
        else if( type.equals("DN")) {
            n = new DN(id,layer);
        }
        if(activation != null) n.activation = activation;
        nodes.put(n.key(), n);
        layers.get(layer).add(n);
        return n;
    }
    public int addLayer()
    {
        List<Node> layer = new ArrayList<>();
        int id = layers.size();
        layers.put(id,layer);
        return id;
    }

    public void reward(float error)
    {
        int layer = layers.size()-1;

        List<Node> nodes = layers.get(layer);
        for(Node n: nodes){
            n.positive(error);
        }

    }

    public void save()
    {
        JsonObject jo = toJsonBrain();
        try {
            FileUtils.writeStringToFile(new File("D:\\projects\\Barin\\data\\brain.json"),jo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node getNode(String key){
        return nodes.get(key);
    }

    public void load()
    {
        System.out.println("Loading...");
        layers = new HashMap<>();
        nodes = new HashMap<>();
        try {
            JsonObject brainObject = Json.createReader( new FileReader("D:\\projects\\Barin\\data\\brain.json")).readObject();
            JsonArray la = brainObject.getJsonArray("layers");
            layers = new HashMap<>();
            for(JsonObject l: la.getValuesAs(JsonObject.class)){
                currentLayer = addLayer();
                JsonArray nodes = l.getJsonArray("nodes");
                for(JsonObject node: nodes.getValuesAs(JsonObject.class)){
                    Node n = addNode( node.getInt("id"), node.getString("type"),currentLayer,null);
                    if( ! node.getString("activation").equals("none")) {
                        if( node.getString("activation").equals("Sigmoid")){

                        }
                    }
                    if( nodeId < n.id) nodeId = n.id;
                    this.nodes.put(n.key(),n);
                    System.out.println("Adding " + n.key() + "  to " + currentLayer);
                }
            }
            JsonArray edges = brainObject.getJsonArray("edges");
            if( edges != null) {
                for (JsonObject edge : edges.getValuesAs(JsonObject.class)) {
                    connectNodes( getNode(edge.getString("source")), getNode(edge.getString("target")), (float)edge.getJsonNumber("weight").doubleValue());
                }
            }
            nodeId++;
            System.out.println("Loaded....");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newLayer(final Node n)
    {
        int currLayer = n.layer;
        int lastLayer = layers.size();
        layers.put(lastLayer,layers.get(lastLayer-1));

        for(int l=lastLayer-1; l> currLayer; l--)
        {
            layers.replace(l,layers.get(l-1));
            for(Node nl: layers.get(l))
            {
                nl.layer = l;
            }
        }
        List<Node> newList = new ArrayList<>();
        layers.replace(currLayer, newList);
        layers.get(currLayer+1).removeIf((nf)->{ return nf.id == n.id;});
        newList.add(n);
    }

    public void punish(float error)
    {
        int layer = layers.size()-1;

        List<Node> nodes = layers.get(layer);
        for(Node n: nodes){
            n.negative(error);
        }
    }


    public void connectNodes(Node source, Node target){
        float weight = 2 * rand.nextFloat();
        if( rand.nextFloat() > 0.5 ) weight = weight * (-1.0f);
        if( source.type == NodeType.DN && target.type == NodeType.DN) weight = 1;
        if( source.type == NodeType.DN && target.type != NodeType.DN) weight = 1;
        connectNodes(source, target, weight);
    }

    public void connectNodes(Node source, Node target, float weight){
        target.connections.add(new NodeConnection(source, weight));
    }

    public void connectLayer(int layer){
        for(Node s: layers.get(layer)){
            for(Node t: layers.get(layer+1)){
                float weight = 2 * rand.nextFloat();
                if( rand.nextFloat() > 0.5 ) weight = weight * (-1.0f);
                if( s.type == NodeType.DN && t.type == NodeType.DN) weight = 1;
                if( s.type == NodeType.DN && t.type != NodeType.DN) weight = 1;
                t.connections.add(new NodeConnection(s, weight));
            }
        }
    }

    public void connectSeeker(DN dn, List<Neuron> nList){
        //Let seekers get some connections
        if( rand.nextBoolean() )
        {
            //Lucky DN
            int count = nList.size();
            int ni = rand.nextInt(count);
            Neuron target = nList.get(ni);
            dn.connections.add( NodeConnection.create(target));
        }
    }
    public DN addSeeker(int layer)
    {
        if( (layers.size() - 1 ) < layer ) addLayer();
        DN sdn = new DN(nodeId++, layer);
        nodes.put(sdn.key(),sdn);
        return sdn;
    }

    public Float in(List<Float> ins)
    {
        seq++;
        //Set the Input
        for(int ii=0;ii<layers.get(0).size();ii++){
            layers.get(0).get(ii).setOutput(seq,ins.get(ii));
        }

        for(Node n: nodes.values())
        {
            if( n.type == NodeType.NI) continue;
            n.calcOutput(seq);
        }

        return layers.get(layers.size()-1).get(0).output;
    }

    public void dumpNode(String key){
        if( key.startsWith("N")) {
            Node n = nodes.get(key);
            System.out.println( n.key() + "  ->  " + n.output);
            for(NodeConnection nc: n.connections){
                System.out.println( "  <-  " + NumberUtil.round( nc.weight, 4)+"," + NumberUtil.round(nc.prevWeight,2) + "----- " + nc.n.key() + "( " + nc.n.output + " )");
            }
        }
    }

    public void initDb()
    {
        if( driver != null) return;
        Properties props = new Properties();
        props.setProperty(DBProperties.SERVER_ROOT_URI, "bolt://localhost:7687");
        // create an IDBAccess instance
        remote =
                DBAccessFactory.createDBAccess(DBType.REMOTE, props, "neo4j", "admin");
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","admin"));

        //String domainName = "BRAIN-DOMAIN";

        g = Graph.create(remote);
    }

    public void delete()
    {
        StatementResult result =  driver.session().run("MATCH(n) DETACH DELETE n");
    }

    public GrNode uploadNeuron(Neuron n){
        //Add input Neurons
        GrNode n1 = g.createNode();
        n1.addLabel("Neuron");
        n1.addProperty("name",n.type.name() + "-" + n.id);
        n1.addProperty("id",n.id );
        n1.addProperty("type",n.type.name());
        n1.addProperty("output",n.output);
        n1.addProperty("seq",n.seq);
        return n1;
    }
    public GrNode uploadDN(DN n){
        //Add input Neurons
        GrNode n1 = g.createNode();
        n1.addLabel("DN");
        n1.addProperty("name",n.type.name() + "-" + n.id);
        n1.addProperty("id",n.id );
        n1.addProperty("type",n.type.name());
        n1.addProperty("output",n.output);
        n1.addProperty("seq",n.seq);
        return n1;
    }

    public GrNode getGrNode(Node s){
        JcNode n = new JcNode("DN");
        String label = "DN";
        if( s.type != NodeType.DN)
        {
            n = new JcNode("Neuron");
            label = "Neuron";
        }
        JcQuery query = new JcQuery();
        query.setClauses( new IClause[]{
                MATCH.node(n).label(label),
                WHERE.valueOf(n.property("id")).EQUALS(s.id),
                RETURN.ALL()
                });
        JcQueryResult result = remote.execute(query);
        return result.resultOf(n).get(0);
    }

    public GrRelation uploadConnection(NodeConnection s, Node t)
    {
        GrNode sNode = getGrNode(s.n);
        GrNode tNode = getGrNode(t);
        GrRelation rel = g.createRelation("Connect",sNode,tNode);
        rel.addProperty("weight", s.weight);
        return rel;
    }

    public JsonObject toJsonBrain(){
        JsonObjectBuilder bo = Json.createObjectBuilder();
        JsonArrayBuilder la = Json.createArrayBuilder();
        JsonArrayBuilder ea = Json.createArrayBuilder();
        for(int l: layers.keySet()){
            List<Node> layer = layers.get(l);
            JsonObjectBuilder lo = Json.createObjectBuilder();
            lo.add("id",l);
            JsonArrayBuilder nodeArray = Json.createArrayBuilder();
            for(Node n: layer){
                JsonObject no = Json.createObjectBuilder()
                        .add("id",n.id)
                        .add("label",n.key())
                        .add("type",n.type.name())
                        .add("output",n.output)
                        .add("key",n.key())
                        .add("layer",n.layer)
                        .add("activation",n.activation == null?"none":n.activation.getClass().getName())
                        .build();
                nodeArray.add(no);

                for(NodeConnection nc: n.connections){
                    JsonObject eo = Json.createObjectBuilder()
                            .add("key",n.key()+":"+nc.n.key())
                            .add("source",n.key())
                            .add("target",nc.n.key())
                            .add("weight",nc.weight)
                            .add("output",nc.n.output)
                            .build();
                    ea.add(eo);
                }
            }
            lo.add("nodes",nodeArray);
            la.add(lo.build());
        }

        bo.add("layers", la);
        bo.add("edges",ea);

        return bo.build();
    }

    public JsonObject toJsonSave(){
        JsonObjectBuilder bo = Json.createObjectBuilder();
        JsonArrayBuilder la = Json.createArrayBuilder();
        JsonArrayBuilder ea = Json.createArrayBuilder();
        for(int l: layers.keySet()){
            List<Node> layer = layers.get(l);
            JsonObjectBuilder lo = Json.createObjectBuilder();
            lo.add("id",l);
            JsonArrayBuilder nodeArray = Json.createArrayBuilder();
            for(Node n: layer){
                JsonObject no = Json.createObjectBuilder()
                        .add("id",n.id)
                        .add("label",n.key())
                        .add("type",n.type.name())
                        .add("output",n.output)
                        .add("key",n.key())
                        .add("layer",n.layer)
                        .build();
                nodeArray.add(no);

                for(NodeConnection nc: n.connections){
                    JsonObject eo = Json.createObjectBuilder()
                            .add("key",n.key()+":"+nc.n.key())
                            .add("source",n.key())
                            .add("target",nc.n.key())
                            .add("weight",nc.weight)
                            .add("output",nc.n.output)
                            .build();
                    ea.add(eo);
                }
            }
            lo.add("nodes",nodeArray);
            la.add(lo.build());
        }

        bo.add("layers", la);
        bo.add("edges",ea);

        return bo.build();
    }
    public JsonObject toJson()
    {
        JsonObjectBuilder bo = Json.createObjectBuilder();

        JsonArrayBuilder ja = Json.createArrayBuilder();
        JsonArrayBuilder ea = Json.createArrayBuilder();
        for(Node n: nodes.values()){
            JsonObject no = Json.createObjectBuilder()
                    .add("id",n.id)
                    .add("label",n.key())
                    .add("type",n.type.name())
                    .add("output",n.output)
                    .add("size",4)
                    .add("x",3)
                    .add("y",3)
                    .add("group", (n.type == NodeType.NI ?1:(n.type == NodeType.NH ?2:3)))
                    .build();
            ja.add(no);
        }

        for(Node n: nodes.values()) {
            for (NodeConnection nc : n.connections) {
                JsonObject eo = Json.createObjectBuilder()
                        .add("key", n.key() + "-" + nc.n.key())
                        .add("source", nc.n.id)
                        .add("target", n.id)
                        .add("weight", nc.weight)
                        .add("output", nc.n.output)
                        .build();
                ea.add(eo);
            }
        }

        bo.add("nodes",ja.build());
        bo.add("links",ea.build());
        JsonObject result = bo.build();
        String outText = result.toString();
        System.out.println(outText);

        try {
            FileUtils.writeStringToFile(new File("D:\\projects\\Barin\\html\\data.json"), outText);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

//    public JsonObject getOneLink(){
//        for(Node n: nodes.values()) {
//            for (NodeConnection nc : n.connections) {
//                JsonObject eo = Json.createObjectBuilder()
//                        .add("key", n.key() + "-" + nc.n.key())
//                        .add("source", nc.n.id)
//                        .add("target", n.id)
//                        .add("weight", nc.weight)
//                        .add("output", nc.n.output)
//                        .build();
//                return eo;
//            }
//        }
//        return null;
//    }

    public void toJson1()
    {
        JsonObjectBuilder bo = Json.createObjectBuilder();

        JsonArrayBuilder ja = Json.createArrayBuilder();
        JsonArrayBuilder ea = Json.createArrayBuilder();

        for(Node n: nodes.values()){
            JsonObjectBuilder do1 = Json.createObjectBuilder();
            JsonObject no = Json.createObjectBuilder()
                    .add("id",n.key())
                    .add("label",n.key())
                    .add("type",n.type.name())
                    .add("output",n.output)
                    .add("size",3)
                    .add("x",3)
                    .add("y",3)
                    .build();
            do1.add("data",no);
            ja.add(do1.build());
        }


        for(Node n: nodes.values()) {
            for (NodeConnection nc : n.connections) {
                JsonObjectBuilder do1 = Json.createObjectBuilder();
                JsonObject eo = Json.createObjectBuilder()
                        .add("id", n.key() + "-" + nc.n.key())
                        .add("source", nc.n.key())
                        .add("target", n.key())
                        .build();
                do1.add("data", eo);
                ja.add(do1.build());
            }
        }

        bo.add("elements",ja.build());
        //Styles
        JsonArrayBuilder sa = Json.createArrayBuilder();
        JsonObjectBuilder so = Json.createObjectBuilder();
        JsonObject sno = Json.createObjectBuilder()
                .add("background-color","#666")
                .add("label","data(id)")
                .build();
        so.add("selector","node");
        so.add("style",sno);
        sa.add(so.build());
        bo.add("style",sa.build());
        String outText = bo.build().toString();
        System.out.println(outText);

        try {
            FileUtils.writeStringToFile(new File("D:\\projects\\Barin\\html\\data1.json"), outText);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
