package com.deneme.influencerinsight.util;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

import java.util.List;

public final class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildAnalysisPrompt(String username,
                                             String userQuestion,
                                             List<SocialMediaAccountEntity> accounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert social media growth analyst.\n");
        sb.append("User handle: ").append(username).append("\n\n");

        sb.append("User question:\n");
        sb.append(userQuestion).append("\n\n");

        sb.append("Available connected accounts and their latest snapshots (JSON):\n");
        for (SocialMediaAccountEntity acc : accounts) {
            sb.append("- Platform: ").append(acc.getPlatform().name()).append("\n");
            if (acc.getExtraData() != null) {
                String trimmed = trim(acc.getExtraData(), 15000);
                sb.append(trimmed).append("\n\n");
            } else {
                sb.append("(no snapshot)\n\n");
            }
        }

        sb.append("Task:\n");
        sb.append("- Provide a concrete, personalized plan. Include:\n");
        sb.append("  1) Best posting times (with timezone assumptions if not present)\n");
        sb.append("  2) Content formats & lengths tailored to the audience\n");
        sb.append("  3) Topic ideas referencing prior performance\n");
        sb.append("  4) Cadence recommendations for the next 2 weeks\n");
        sb.append("  5) Any channel hygiene or packaging improvements (titles, thumbnails, tagging)\n");
        sb.append("- If data is missing (e.g., watch-time or CTR), state assumptions explicitly.\n");
        sb.append("- Return the answer in Turkish.\n");

        return sb.toString();
    }

    private static String trim(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "\n... (truncated)";
    }
}
