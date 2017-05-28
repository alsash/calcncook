package com.alsash.reciper.logic;

import com.alsash.reciper.data.cloud.CloudManager;
import com.alsash.reciper.data.db.DbManager;
import com.alsash.reciper.logic.error.NoInternetException;

import java.util.Date;
import java.util.Locale;

/**
 * Logic for processing data in storage
 */
public class StorageLogic {

    private final CloudManager cloudManager;
    private final DbManager dbManager;

    public StorageLogic(DbManager dbManager, CloudManager cloudManager) {
        this.dbManager = dbManager;
        this.cloudManager = cloudManager;
    }

    public void createStartupEntitiesIfNeed() throws NoInternetException {
        Date localUpdateDate = dbManager.getSettingsUpdateDate();
        Date cloudUpdateDate = cloudManager.getDbUpdateDate();
        boolean create = false;
        // First access
        if (localUpdateDate == null) {
            if (cloudUpdateDate != null) {
                create = true;
            } else if (!cloudManager.isOnline()) {
                throw new NoInternetException();
            } else {
                create = false; // Something goes wrong, but we can't do anything...
            }
            // Next access
        } else {
            create = false;
            if (cloudUpdateDate != null) {
                create = localUpdateDate.getTime() < cloudUpdateDate.getTime();
            }
        }
        if (createStartupEntities()) {
            dbManager.setSettingsUpdateDate(new Date());
        }
    }

    private boolean createStartupEntities() {
        String pathDbLanguage = cloudManager.getPathDbLanguage(Locale.getDefault());
        if (pathDbLanguage == null) pathDbLanguage = cloudManager.getPathDbLanguage(Locale.ENGLISH);
        if (pathDbLanguage == null) return false;
        dbManager.insertAuthors(cloudManager.getAuthors(pathDbLanguage));

        return false;
    }

}
