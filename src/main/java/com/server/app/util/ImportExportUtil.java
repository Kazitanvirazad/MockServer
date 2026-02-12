package com.server.app.util;

import com.server.app.dto.CollectionDto;
import com.server.app.model.data.Collection;
import com.server.app.model.data.Server;
import com.server.app.service.CollectionService;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.app.constants.ApplicationConstants.UNDERSCORE;
import static com.server.app.util.AppUtil.getRandomNumberInRange;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.Serializer.deSerializeList;
import static com.server.app.util.Serializer.serializeList;

public class ImportExportUtil {
    private static final Logger log = LogManager.getLogger(ImportExportUtil.class);
    private final ServerService serverService;
    private final CollectionService collectionService;

    public ImportExportUtil() {
        this.serverService = Service.INSTANCE.getServerService();
        this.collectionService = Service.INSTANCE.getCollectionService();
    }

    public void exportCollection(File selectedDirectory, List<Collection> collections) {
        try {
            List<CollectionDto> exportData = collections.stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .map(collection -> {
                        List<Server> servers = serverService.getServersByCollection(collection.getCollectionId())
                                .collect(Collectors.toList());
                        collection.setServers(servers);
                        return new CollectionDto(collection);
                    }).toList();
            Optional<String> optionalCollectionJson = serializeList(exportData);
            if (FileUtils.isDirectory(selectedDirectory) && optionalCollectionJson.isPresent()) {
                int randomNumberInRange = getRandomNumberInRange(999, 3999);
                File output = new File(selectedDirectory.getAbsolutePath() + "\\collection_"
                        + randomNumberInRange + ".json");
                FileUtils.writeStringToFile(output, optionalCollectionJson.get(), StandardCharsets.UTF_8);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            triggerErrorAlert("Something went wrong!", "Please try again later");
        }
    }

    public boolean importCollection(File selectedCollectionFile) {
        try {
            String inputJson = FileUtils.readFileToString(selectedCollectionFile, StandardCharsets.UTF_8);
            Optional<List<CollectionDto>> optionalCollections = deSerializeList(inputJson, CollectionDto.class);
            if (optionalCollections.isEmpty()) {
                triggerErrorAlert("Collection import failed!", "Insufficient input data");
                return false;
            }
            List<CollectionDto> collectionDtos = optionalCollections.get();
            if (CollectionUtils.isEmpty(collectionDtos)) {
                triggerErrorAlert("Collection import failed!", "Collection not found in file");
                return false;
            }
            List<Collection> collections = collectionDtos.stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .filter(collectionDto -> StringUtils.isNotBlank(collectionDto.collectionName()))
                    .map(Collection::new)
                    .toList();
            if (CollectionUtils.isEmpty(collections)) {
                triggerErrorAlert("Collection import failed!", "Collections are empty");
                return false;
            }
            for (Collection collection : collections) {
                Optional<Collection> optionalExisting = collectionService.getCollectionByName(collection.getCollectionName());
                if (optionalExisting.isPresent()) {
                    int randomNumberInRange = getRandomNumberInRange(999, 5999);
                    String keepingCollectionName = collection.getCollectionName() + UNDERSCORE + randomNumberInRange;
                    collection.setCollectionName(keepingCollectionName);
                }
                Optional<String> optionalImportedCollectionId = collectionService.createImportedCollection(collection);
                if (optionalImportedCollectionId.isPresent() && CollectionUtils.isNotEmpty(collection.getServers())) {
                    List<Server> servers = collection.getServers();
                    servers.forEach(server -> {
                        Optional<String> optionalServerId = serverService.createImportedServer(server, optionalImportedCollectionId.get());
                    });
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
            triggerErrorAlert("Collection import failed!", "Something went wrong");
            return false;
        }
        return true;
    }
}
