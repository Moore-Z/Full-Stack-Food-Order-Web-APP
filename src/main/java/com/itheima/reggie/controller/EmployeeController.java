package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){


        // md5 processs
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2. use username check database
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3. IF not get return error
        if (emp==null){
            return R.error("Login failed, username not exist");
        }

        //4. Compare password
        if (!emp.getPassword().equals(password)){
            return R.error("Login failed,wrong password");
        }


        //5. check status of account
        if(emp.getStatus() ==0){
            return R.error("Login failed, account freezed");
        }

        //6. Login success
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("Logout success!~~~~~~~");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody  Employee employee){

        log.info("The added employee: {}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 获得创建用户id
//        Long emp = (Long)request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(emp);
//        employee.setUpdateUser(emp);

        employeeService.save(employee);

        return R.success("SuccessFully add new Employee");
        //return null;
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("------------------page= {},  pageSize = {}, name = {}----------",page,pageSize,name);

        // Page constructor
        Page PageInfo = new Page(page, pageSize);

        // Condition constructor
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // Add Conditional Order
        queryWrapper.orderByDesc(Employee::getCreateTime);

        // Process check
        employeeService.page(PageInfo, queryWrapper);


        return R.success(PageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

//        Long user = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(user);
        employeeService.updateById(employee);
        return R.success("Employee update success");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        log.info("find employee by ID: {}",id);
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("No employee info with provided ID");
    }
}
