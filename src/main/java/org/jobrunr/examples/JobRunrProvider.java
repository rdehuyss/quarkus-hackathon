package org.jobrunr.examples;

import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.sqlite.SqLiteStorageProvider;
import org.jobrunr.utils.mapper.JsonMapper;
import org.jobrunr.utils.mapper.jsonb.JsonbJsonMapper;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import javax.sql.DataSource;

import static org.jobrunr.server.BackgroundJobServerConfiguration.usingStandardConfiguration;

public class JobRunrProvider {

    @Produces
    @Singleton
    public JobRunrDashboardWebServer dashboardWebServer(StorageProvider storageProvider, JsonMapper jsonMapper) {
        return new JobRunrDashboardWebServer(storageProvider, jsonMapper);
    }

    @Produces
    @Singleton
    public BackgroundJobServer backgroundJobServer(StorageProvider storageProvider, JobActivator jobActivator) {
        return new BackgroundJobServer(storageProvider, jobActivator, usingStandardConfiguration().andWorkerCount(4));
    }

    @Produces
    @Singleton
    public JobActivator jobActivator() {
        return new JobActivator() {
            @Override
            public <T> T activateJob(Class<T> aClass) {
                return CDI.current().select(aClass).get();
            }
        };
    }

    @Produces
    @Singleton
    public JobScheduler jobScheduler(StorageProvider storageProvider) {
        final JobScheduler jobScheduler = new JobScheduler(storageProvider);
        BackgroundJob.setJobScheduler(jobScheduler);
        return jobScheduler;
    }

    @Produces
    @Singleton
    public StorageProvider storageProvider(DataSource dataSource, JobMapper jobMapper) {
        final SqLiteStorageProvider sqLiteStorageProvider = new SqLiteStorageProvider(dataSource);
        sqLiteStorageProvider.setJobMapper(jobMapper);
        return sqLiteStorageProvider;
    }

    @Produces
    @Singleton
    public JobMapper jobMapper(JsonMapper jsonMapper) {
        return new JobMapper(jsonMapper);
    }

    @Produces
    @Singleton
    public JsonMapper jsonMapper() {
        return new JsonbJsonMapper();
    }
}
