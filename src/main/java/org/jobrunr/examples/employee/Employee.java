package org.jobrunr.examples.employee;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.stream.Stream;

@Entity
public class Employee extends PanacheEntity {
    public String firstName;
    public String lastName;
    public String email;
    public LocalDate birthDate;

    public static Stream<Long> streamAllIds() {
        final Stream<Employee> stream = findAll().stream();
        return stream.map(x -> x.id);
    }

    @RegisterForReflection
    public class Id {
        public Long id;
    }
}
