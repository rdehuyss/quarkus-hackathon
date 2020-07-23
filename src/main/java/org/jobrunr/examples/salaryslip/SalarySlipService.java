package org.jobrunr.examples.salaryslip;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import org.jobrunr.examples.employee.Employee;
import org.jobrunr.examples.email.EmailService;
import org.jobrunr.examples.timeclock.TimeClockService;
import org.jobrunr.examples.timeclock.WorkWeek;
import org.jobrunr.jobs.JobDetails;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.BackgroundJob;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.LocalDate.now;

@ApplicationScoped
public class SalarySlipService {


    @ResourcePath("salaryslip.html")
    Template salarySlipTemplate;

    @Inject
    TimeClockService timeClockService;
    @Inject
    DocumentGenerationService documentGenerationService;
    @Inject
    EmailService emailService;

    @Job(name = "Generate and send salary slip to all employees")
    @ActivateRequestContext
    public void generateAndSendSalarySlipToAllEmployees() {
        final Stream<Long> allEmployeeIds = Employee.streamAllIds();
        BackgroundJob.<SalarySlipService, Long>enqueue(allEmployeeIds, (salarySlipService, employeeId) -> salarySlipService.generateAndSendSalarySlip(employeeId));
    }

    @Job(name = "Generate and send salary slip to employee %0")
    @ActivateRequestContext
    public void generateAndSendSalarySlip(Long employeeId) throws Exception {
        final Employee employee = getEmployee(employeeId);
        Path salarySlipPath = generateSalarySlip(employee);
        emailService.sendSalarySlip(employee, salarySlipPath);
    }

    private Path generateSalarySlip(Employee employee) throws Exception {
        final WorkWeek workWeek = getWorkWeekForEmployee(employee.id);
        final SalarySlip salarySlip = new SalarySlip(employee, workWeek);
        return generateSalarySlipDocumentUsingTemplate(salarySlip);
    }

    private Path generateSalarySlipDocumentUsingTemplate(SalarySlip salarySlip) throws Exception {
        Path salarySlipPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(now().getYear()), format("workweek-%d", salarySlip.getWorkWeek().getWeekNbr()), format("salary-slip-employee-%d.pdf", salarySlip.getEmployee().id));
        documentGenerationService.generateDocument(salarySlipTemplate, salarySlipPath, salarySlip);
        return salarySlipPath;
    }

    private WorkWeek getWorkWeekForEmployee(Long employeeId) {
        return timeClockService.getWorkWeekForEmployee(employeeId);
    }

    private Employee getEmployee(Long employeeId) {
        return Employee.findById(employeeId);
    }

}
