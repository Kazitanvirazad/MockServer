package com.server.core.util;

import com.server.core.config.CommonConfig;
import com.server.core.dto.CollectionDto;
import com.server.core.model.data.Collection;
import com.server.core.model.data.Server;
import com.server.core.service.CollectionService;
import com.server.core.service.ServerService;
import com.server.core.service.Service;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.core.constants.CommonConstants.UNDERSCORE;
import static com.server.core.util.CommonUtil.getRandomNumberInRange;
import static com.server.core.util.Serializer.deSerializeList;
import static com.server.core.util.Serializer.serializeList;

/**
 * @author Kazi Tanvir Azad
 */
public class ImportExportUtil {
    private static final Logger log = LogManager.getLogger(ImportExportUtil.class);
    private final ServerService serverService;
    private final CollectionService collectionService;

    public ImportExportUtil() {
        this.serverService = Service.INSTANCE.getServerService();
        this.collectionService = Service.INSTANCE.getCollectionService();
    }

    /**
     * Utility method to export collection list passed in the argument to a file which has been passed in the argument
     *
     * @param selectedDirectory {@link File}
     * @param collections       {@link List}
     */
    public void exportCollection(File selectedDirectory, List<Collection> collections) {
        try {
            // converts the data to be exported
            List<CollectionDto> exportData = collections.stream()
                    .filter(Objects::nonNull)
                    .map(collection -> {
                        List<Server> servers = serverService.getServersByCollection(collection.getCollectionId())
                                .collect(Collectors.toList());
                        collection.setServers(servers);
                        return new CollectionDto(collection);
                    }).toList();
            // json serialization of the export data
            Optional<String> optionalCollectionJson = serializeList(exportData);
            if (FileUtils.isDirectory(selectedDirectory) && optionalCollectionJson.isPresent()) {
                int randomNumberInRange = getRandomNumberInRange(999, 3999);
                // Creating the export file name by concatenating with random number to avoid file overwrite
                File output = new File(selectedDirectory.getAbsolutePath() + SystemProperties.getFileSeparator()
                        + "collection_" + randomNumberInRange + ".json");
                // writing the file to the specified directory
                FileUtils.writeStringToFile(output, optionalCollectionJson.get(), StandardCharsets.UTF_8);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Something went wrong!", "Please try again later");
        }
    }

    /**
     * Utility method to import collections for the file passed in the method argument
     *
     * @param selectedCollectionFile {@link File}
     * @return True if importing is successfully done, otherwise False {@code  boolean}
     */
    public boolean importCollection(File selectedCollectionFile) {
        try {
            // Reading the collection file json content to be imported
            String inputJson = FileUtils.readFileToString(selectedCollectionFile, StandardCharsets.UTF_8);
            // deserializing the json to the collection list
            Optional<List<CollectionDto>> optionalCollections = deSerializeList(inputJson, CollectionDto.class);
            if (optionalCollections.isEmpty()) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Collection import failed!", "Insufficient input data");
                return false;
            }
            List<CollectionDto> collectionDtos = optionalCollections.get();
            if (CollectionUtils.isEmpty(collectionDtos)) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Collection import failed!", "Collection not found in file");
                return false;
            }
            List<Collection> collections = collectionDtos.stream()
                    .filter(Objects::nonNull)
                    .filter(collectionDto -> StringUtils.isNotBlank(collectionDto.collectionName()))
                    .map(Collection::new)
                    .toList();
            if (CollectionUtils.isEmpty(collections)) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Collection import failed!", "Collections are empty");
                return false;
            }
            for (Collection collection : collections) {
                Optional<Collection> optionalExisting = collectionService.getCollectionByName(collection.getCollectionName());
                // appending the imported collection name with random number to avoid overwriting
                // in case collection exists with the same name
                if (optionalExisting.isPresent()) {
                    int randomNumberInRange = getRandomNumberInRange(999, 5999);
                    String keepingCollectionName = collection.getCollectionName() + UNDERSCORE + randomNumberInRange;
                    collection.setCollectionName(keepingCollectionName);
                }
                Optional<String> optionalImportedCollectionId = collectionService.createImportedCollection(collection);
                if (optionalImportedCollectionId.isPresent() && CollectionUtils.isNotEmpty(collection.getServers())) {
                    List<Server> servers = collection.getServers();
                    // creating all the servers present in the imported collection
                    servers.forEach(server -> serverService.createImportedServer(server, optionalImportedCollectionId.get()));
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Collection import failed!", "Something went wrong");
            return false;
        }
        return true;
    }

    public byte[] readFile(String fileAbsolutePath) {
        try {
            File file = new File(fileAbsolutePath);
            return FileUtils.readFileToByteArray(file);
        } catch (Exception exception) {
            return new byte[0];
        }
    }
}
