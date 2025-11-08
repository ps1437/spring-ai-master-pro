package com.syscho.ai.mcp.tools;

import com.syscho.ai.external.db.EmployeeRepository;
import com.syscho.ai.external.db.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeTool {

    private final EmployeeRepository employeeService;

    @Tool(
            name = "get_employees_by_department",
            description = "Fetch all employees in a department from the business layer"
    )
    public List<Employee> getEmployees(String departmentName) {
        return employeeService.findByDepartment_Name(departmentName);
    }

    @Tool(
            name = "get_employees",
            description = "Fetch all employees"
    )
    public List<Employee> getEmployees() {
        return employeeService.findAll();
    }

}
