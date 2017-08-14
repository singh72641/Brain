package com.punwire.brain.web;


import com.google.inject.Inject;
import com.punwire.brain.BarinConfig;
import com.punwire.brain.Brain;
import com.punwire.brain.DN;
import org.jooby.WebSocket;
import org.jooby.mvc.Path;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.StringReader;

@Path("/brain")
public class BrainSocket implements WebSocket.OnMessage<String>{

    WebSocket ws;

    @Inject
    public BrainSocket(WebSocket ws) {
        this.ws = ws;
    }

    @Override
    public void onMessage(String s) throws Exception {
        Brain brain = BarinConfig.get();
        System.out.println("Got: " + s);
        if( s.equals("START")) {
            JsonObjectBuilder ob = Json.createObjectBuilder();
            ob.add("template","brain.html");
            ob.add("action","LOAD");
            ob.add("data", brain.toJson());
            ws.send(ob.build().toString());
        }
        else {
            JsonObject msg = Json.createReader(new StringReader(s)).readObject();

            if (msg.getString("message").equals("UPDATE")) {
                JsonObjectBuilder ob = Json.createObjectBuilder();
                //JsonObject dn = brain.getOneLink();
                ob.add("action", "LINK_UPD");
                //ob.add("data", dn.getString("key"));
                ws.send(ob.build().toString());
            } else if (msg.getString("message").equals("NEXT")) {
                BarinConfig.train.next();
                JsonObjectBuilder ob = Json.createObjectBuilder();
                //JsonObject dn = brain.getOneLink();
                ob.add("action", "NEXT");
                ob.add("data", brain.toJson());
                ws.send(ob.build().toString());
            }
        }
    }
}
