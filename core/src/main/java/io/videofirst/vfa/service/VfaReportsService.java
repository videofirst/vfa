package io.videofirst.vfa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.properties.VfaReportsProperties;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Reports service.
 *
 * @author Bob Marks
 */
@Singleton
public class VfaReportsService {

    @Inject
    private ObjectMapper objectMapper;

    // Injected fields

    private final VfaReportsProperties reportsProperties;

    // Other fields

    private File reportsFolder;

    @Inject
    public VfaReportsService(VfaReportsProperties reportsProperties) {
        this.reportsProperties = reportsProperties;

        // Set reports folder and create folders if they don't exist
        this.reportsFolder = new File(reportsProperties.getFolder()).getAbsoluteFile();
        reportsFolder.mkdirs();
    }

    public void initFeatureFolder(VfaFeature feature) {
        try {
            File featureFolder = getFeatureFolder(feature);
            featureFolder.mkdirs();
            FileUtils.cleanDirectory(featureFolder);
        } catch (IOException e) {
            throw new VfaException("Error deleting reports folder: " + reportsFolder.getAbsolutePath());
        }
    }

    public File getFeatureFolder(VfaFeature feature) {
        return new File(reportsFolder, feature.getClassName());
    }

    public void saveFeature(VfaFeature feature) {
        String filename = "VFA-" + feature.getClassName() + ".json";
        File featureFolder = getFeatureFolder(feature);
        File file = new File(featureFolder, filename);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, feature);
        } catch (IOException e) {
            throw new VfaException("Issue writing file: " + file.getAbsolutePath(), e);
        }
    }

}
