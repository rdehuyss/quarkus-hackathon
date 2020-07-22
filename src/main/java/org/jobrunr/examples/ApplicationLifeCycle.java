package org.jobrunr.examples;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.jobrunr.examples.salaryslip.SalarySlipService;
import org.jobrunr.jobs.JobDetails;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.server.BackgroundJobServer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.DayOfWeek;

@ApplicationScoped
public class ApplicationLifeCycle {

    @Inject
    BackgroundJobServer backgroundJobServer;
    @Inject
    JobScheduler jobScheduler;
    @Inject
    SalarySlipService salarySlipService;


    void onStart(@Observes StartupEvent ev) {
        System.out.println("The application is starting...");
        System.out.println("Starting background job server");
        backgroundJobServer.start();
        System.out.println("Initializing job scheduler: " + jobScheduler.getClass().getName());
        backgroundJobServer.start();
        System.out.println("Creating recurring jobs");
        BackgroundJob.scheduleRecurringly(
                "generate-and-send-salary-slip",
                //() -> salarySlipService.generateAndSendSalarySlipToAllEmployees(),
                new JobDetails(salarySlipService, "generateAndSendSalarySlipToAllEmployees"),
                Cron.weekly(DayOfWeek.SUNDAY, 22));
    }

    void onStop(@Observes ShutdownEvent ev) {
        System.out.println("Shutting down...");
    }

}
