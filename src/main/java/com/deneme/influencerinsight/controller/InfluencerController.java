package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.model.Influencer;
import com.deneme.influencerinsight.service.InfluencerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/influencers")
public class InfluencerController {

    private final InfluencerService service;

    public InfluencerController(InfluencerService service) {
        this.service = service;
    }

    @PostMapping
    public Influencer create(@RequestBody Influencer influencer) {
        return service.save(influencer);
    }

    @GetMapping
    public List<Influencer> getAll() {
        return service.getAll();
    }
}
