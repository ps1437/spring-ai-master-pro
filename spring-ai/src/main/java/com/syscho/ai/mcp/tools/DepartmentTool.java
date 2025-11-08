package com.syscho.ai.mcp.tools;

import com.syscho.ai.external.db.DepartmentRepository;
import com.syscho.ai.external.db.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DepartmentTool {
    private final DepartmentRepository departmentRepository;

    @Tool(
            name = "get_department_info",
            description = "Get department details (name, location, and employee count) from the H2 database"
    )
    public Optional<Department> getDepartmentInfo(String departmentName) {
        return departmentRepository.findByName(departmentName);
    }
}
