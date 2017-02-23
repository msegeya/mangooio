package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;

import interfaces.Constants;
import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import models.Fortune;
import models.Message;
import models.World;
import services.DataService;
import utils.RandomUtils;

/**
 *
 * @author svenkubiak
 *
 */
public class ApplicationController {
    private final DataService dataService;
    private final Message message = new Message(Constants.HELLO_WORLD);
    private final Fortune fortune = new Fortune(0, Constants.FORTUNE_MESSAGE);

    @Inject
    public ApplicationController(DataService dataService) {
        this.dataService = dataService;
    }

    @Routing(method = "GET", url = "/json")
    public Response json() {
        return Response.withOk()
                .andJsonBody(message);
    }

    @Routing(method = "GET", url = "/db")
    public Response db() {
        final World world = dataService.findById(RandomUtils.getRandomId());
        
        return Response.withOk()
                .andJsonBody(world);
    }

    @Routing(method = "GET", url = "/queries")
    public Response queries() {
        final List<World> worlds = dataService.findWorlds(RandomUtils.getRandomWorlds());
        return Response.withOk()
                .andJsonBody(worlds);
    }
    
    @Routing(method = "GET", url = "/fortunes")
    public Response fortunes() {
        final List<Fortune> fortunes = dataService.findAllFortunes();
        fortunes.add(fortune);
        Collections.sort(fortunes);

        return Response.withOk()
                .andContent("fortunes", fortunes);
    }

    @Routing(method = "GET", url = "/updates")
    public Response updates() {
        List<World> worlds = dataService.findWorlds(RandomUtils.getRandomWorlds());
        for (World world : worlds) {
            world.setRandomnumber(RandomUtils.getRandomId());
            dataService.save(world);
        }

        final List<World> output = new ArrayList<>();
        output.addAll(worlds);
        
        return Response.withOk()
                .andJsonBody(output);
    }
    
    @Routing(method = "GET", url = "/plaintext")
    public Response plaintext() {
        return Response.withOk()
                .andTextBody(Constants.HELLO_WORLD);
    }
}