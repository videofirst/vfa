package io.videofirst.vfa.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.enums.VfaReportMedia;
import lombok.Data;

/**
 * Configuration associated with reporting.
 */
@Data
@ConfigurationProperties("vfa.reports")
@Context
public class VfaReportsProperties {

    // Injected config

    private VfaReportMedia media;

    private String folder;

}