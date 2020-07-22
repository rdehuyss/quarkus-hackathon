package org.jobrunr.examples.webapp.api;

import org.jobrunr.examples.employee.Employee;
import org.jobrunr.examples.salaryslip.SalarySlipService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;

@Path("jobs")
@ApplicationScoped
public class JobResource {

    @Inject
    SalarySlipService salarySlipService;

    @GET
    @Path("/test-doc-generation")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SimpleResponse persistEmployees(@DefaultValue("1000") @QueryParam("amount") int amount) throws Exception {
        final Employee employee = new Employee();
        employee.firstName = "Test";
        employee.lastName = "The Tester";
        employee.email = "test.thetester@gmail.com";
        employee.birthDate = LocalDate.of(1981,10,14);

        employee.persist();

        salarySlipService.generateAndSendSalarySlip(employee.id);

        return new SimpleResponse("Generated salary slip for employee " + employee.id);
    }

}
