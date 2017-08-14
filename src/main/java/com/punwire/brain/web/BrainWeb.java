package com.punwire.brain.web;

import com.punwire.brain.Brain;
import org.jooby.Jooby;
import scala.App;

import static spark.Spark.*;

/**
 * Created by admin on 7/18/2017.
 */
public class BrainWeb extends Jooby {

    public BrainWeb(){

        assets("/assets/**");
        assets("/", "test.html");
        ws(BrainSocket.class);
    }
    public static void main(String[] args) {
        run(BrainWeb::new, args);
    }
}
