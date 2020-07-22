package org.jobrunr.examples.webapp.api;

import org.jobrunr.examples.employee.Employee;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;

@Path("employees")
@ApplicationScoped
public class EmployeeResource {

    @GET
    @Path("/persist-employees")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SimpleResponse persistEmployees(@DefaultValue("1000") @QueryParam("amount") int amount) {

        for (int i = 0; i < amount; i++) {
            Employee employee = new Employee();
            employee.firstName = "employeeFirstName" + i;
            employee.lastName = "employeeLastName";
            employee.birthDate = LocalDate.of(1981, 10, 14);
            employee.email = employee.firstName + "." + employee.lastName + "@gmail.com";
            employee.persist();
        }

        return new SimpleResponse("Employees saved (total: " + Employee.count() + ")");
    }
}
