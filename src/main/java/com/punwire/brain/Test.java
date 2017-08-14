package com.punwire.brain;

import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.domain.DomainAccessFactory;
import iot.jcypher.domain.IDomainAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.graph.GrRelation;
import iot.jcypher.graph.Graph;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.result.JcError;
import iot.jcypher.query.values.JcNode;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.util.*;

/**
 * Created by admin on 7/14/2017.
 */
public class Test {
    Driver driver;
    IDBAccess remote;
    Graph g;

    public void init()
    {
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

    public void dump()
    {
        StatementResult result =  driver.session().run("MATCH(n:Neuron) RETURN n");
        while(result.hasNext())
        {
            Record row = result.next();
            Value neuron = row.get(0);
            System.out.println(neuron.get("name"));
            Map<String,Object> param = new HashMap<>();
            param.put("id",neuron.get("id").asInt());
            driver.session().run("MATCH(n:Neuron) WHERE n.id = $id  DETACH DELETE n ",param);
        }
    }

    public void connectNeuron(Neuron s, Neuron t)
    {

    }

    public void createNeuron(Neuron n)
    {
        GrNode n1 = g.createNode();
        n1.addLabel("Neuron");
        n1.addProperty("name",n.type + "-1" + n.id);
        n1.addProperty("id",n.id );
        n1.addProperty("type",n.type);

        GrNode a1 = g.createNode();
        a1.addLabel("Axiom");
        a1.addProperty("name","Axiom-"+n.id);
        //a1.addProperty("output",n.axiom.value);

        GrRelation r1 = g.createRelation("Out",n1,a1);
    }

    public void create() {

        Map<Integer,Neuron> neurons = new HashMap<>();
        for(int i=0;i<10;i++){
            //Neuron n = new Neuron(i,"Input");
            //neurons.put(i,n);
        }

        for(Neuron n: neurons.values()){

        }
            List<JcError> errors =  g.store();

            if( errors.size() > 0){
                System.out.println(errors.get(0).getMessage());
            }
    }
    public static void main(String[] args) {
        try {
            Test test = new Test();
            test.init();
            test.delete();
            test.create();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
