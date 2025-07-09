package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.Influencer;

import java.util.List;

public interface InfluencerService {

    Influencer save(Influencer influencer);

    List<Influencer> getAll();
}
