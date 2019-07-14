package com.f1soft.departmentservice.unittest;

import com.f1soft.departmentservice.entities.Department;
import com.f1soft.departmentservice.exception.BadRequestDataException;
import com.f1soft.departmentservice.exception.DataAlreadyExistsException;
import com.f1soft.departmentservice.exception.DataNotFoundException;
import com.f1soft.departmentservice.exception.NoChangeFoundException;
import com.f1soft.departmentservice.repository.DepartmentRepository;
import com.f1soft.departmentservice.requestDTO.DepartmentSetupDTO;
import com.f1soft.departmentservice.requestDTO.UpdatedDepartmentDTO;
import com.f1soft.departmentservice.service.serviceimpl.DepartmentServiceImpl;
import com.f1soft.departmentservice.utils.DepartmentUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
/**
 * @author Sauravi
 */

@RunWith(MockitoJUnitRunner.class)
public class DepartmentServiceImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    DepartmentRepository departmentRepository;

    private DepartmentServiceImpl departmentService;

    private DepartmentSetupDTO departmentSetupDTO;

    private Department savedDepartment;

    private UpdatedDepartmentDTO updatedDepartmentDTO;

    @Before
    public void setUp() {
        departmentService = new DepartmentServiceImpl(departmentRepository);
        departmentSetupDTO = DepartmentSetupDTO.builder()
                .departmentName("Surgical")
                .code("SRG")
                .status('Y')
                .build();

        updatedDepartmentDTO=new UpdatedDepartmentDTO();
        updatedDepartmentDTO.setId(1L);
        updatedDepartmentDTO.setDepartmentName("Sugery");
        updatedDepartmentDTO.setCode("SRG");
        updatedDepartmentDTO.setStatus('Y');


        savedDepartment = new Department();
        savedDepartment.setId(1L);
        savedDepartment.setDepartmentName("Surgical");
        savedDepartment.setCode("SRG");
        savedDepartment.setStatus('Y');

    }

    @Test
    public void addDepartment() {
        addDepartment_ShouldReturnNameExists();
        addDepartment_ShouldReturnCodeExists();
        addDepartment_ShouldSaveData();
        addDepartment_ShhouldReturnBadRequest();
    }

    @Test
    public void fetchAllDepartment(){
        fetchAllDepartment_ShouldReturnEmptyList();
        fetchAllDepartment_ShouldReturnDepartments();
    }

    @Test
    public void deleteDeparment(){
        deleteDepartment_ShouldReturnDataNotFound();
        deleteDepartment_ShouldDeleteData();
    }

    @Test
    public void updateDepartment(){
        updateDepartment_ShouldReturnDataNotFound();
        updateDepartment_ShouldReturnNoChangeFound();
        updateDepartment_ShouldUpdateDepartment();
    }


    @Test
    public void addDepartment_ShouldReturnNameExists() {

        thrown.expect(DataAlreadyExistsException.class);
        thrown.expectMessage("DEPARTMENT WITH NAME :" + " " + departmentSetupDTO.getDepartmentName() + " " + " ALREADY EXISTS");

        given(departmentRepository.findByName(departmentSetupDTO.getDepartmentName())).willReturn(1);

        departmentService.addDepartment(departmentSetupDTO);

    }

    @Test
    public void addDepartment_ShouldReturnCodeExists() {
        thrown.expect(DataAlreadyExistsException.class);
        thrown.expectMessage("DEPARTMENT WITH CODE :" + " " + departmentSetupDTO.getCode() + " " + " ALREADY EXISTS");

        given(departmentRepository.findByName(departmentSetupDTO.getDepartmentName())).willReturn(null);
        given(departmentRepository.findByCode(departmentSetupDTO.getCode())).willReturn(1);

        departmentService.addDepartment(departmentSetupDTO);
    }

    @Test
    public void addDepartment_ShouldSaveData() {
        Department department = DepartmentUtils.convertDepartmentSetupToDepartment(departmentSetupDTO);

        given(departmentRepository.findByName(departmentSetupDTO.getDepartmentName())).willReturn(null);
        given(departmentRepository.findByCode(departmentSetupDTO.getCode())).willReturn(null);
        given(departmentRepository.save(department)).willReturn(savedDepartment);

        assertThat(departmentService.addDepartment(departmentSetupDTO)).isEqualTo(savedDepartment);
        verify(departmentRepository).save(department);
    }

    @Test
    public void addDepartment_ShhouldReturnBadRequest() {
        thrown.expect(BadRequestDataException.class);
        thrown.expectMessage("Bad Request.");

        departmentService.addDepartment(null);
    }

    @Test
    public void fetchAllDepartment_ShouldReturnEmptyList(){
        thrown.expect(DataNotFoundException.class);
        thrown.expectMessage("SORRY, DEPARTMENT NOT FOUND");

        given(departmentRepository.fetchAllDepartment()).willReturn(Collections.emptyList());

        departmentService.fetchAllDepartment();
    }

    @Test
    public void fetchAllDepartment_ShouldReturnDepartments(){
        List<Department> departmentList=new ArrayList<>();
        departmentList.add(savedDepartment);

        given(departmentRepository.fetchAllDepartment()).willReturn(departmentList);

        assertThat(departmentService.fetchAllDepartment()).isEqualTo(departmentList);
        assertThat(departmentService.fetchAllDepartment()).hasSize(1);
    }

    @Test
    public void deleteDepartment_ShouldReturnDataNotFound(){
        thrown.expect(DataNotFoundException.class);
        thrown.expectMessage("SORRY, DEPARTMENT NOT FOUND");

        given(departmentRepository.findByDepartmentId(1L)).willReturn(null);

        departmentService.deleteDepartment(1L);
    }

    @Test
    public void deleteDepartment_ShouldDeleteData(){
        Department departmentToSave=DepartmentUtils.convertDepartmentToDelete(savedDepartment);

        given(departmentRepository.findByDepartmentId(1L)).willReturn(savedDepartment);
        given(departmentRepository.save(departmentToSave)).willReturn(departmentToSave);

        assertThat(departmentService.deleteDepartment(savedDepartment.getId())).isEqualTo(departmentToSave);
        verify(departmentRepository).save(departmentToSave);
    }

    @Test
    public void updateDepartment_ShouldReturnDataNotFound(){
        thrown.expect(DataNotFoundException.class);
        thrown.expectMessage("SORRY, DEPARTMENT NOT FOUND");

        given(departmentRepository.findByDepartmentId(updatedDepartmentDTO.getId())).willReturn(null);

        departmentService.updateDepartment(updatedDepartmentDTO);
    }

    @Test
    public void updateDepartment_ShouldReturnNoChangeFound(){
        thrown.expect(NoChangeFoundException.class);
        thrown.expectMessage("SORRY, NO CHANGES FOUND");

        given(departmentRepository.findByDepartmentId(updatedDepartmentDTO.getId())).willReturn(savedDepartment);
        given(departmentRepository.searchDepartment(updatedDepartmentDTO.getId(),
                updatedDepartmentDTO.getDepartmentName(),updatedDepartmentDTO.getCode(),updatedDepartmentDTO.getStatus())).willReturn(savedDepartment);

        departmentService.updateDepartment(updatedDepartmentDTO);

    }

    @Test
    public void updateDepartment_ShouldUpdateDepartment(){
        Department departmentToSave=DepartmentUtils.convertDepartmentToUpdate(updatedDepartmentDTO,savedDepartment);
        given(departmentRepository.findByDepartmentId(updatedDepartmentDTO.getId())).willReturn(savedDepartment);
        given(departmentRepository.searchDepartment(updatedDepartmentDTO.getId(),
                updatedDepartmentDTO.getDepartmentName(),updatedDepartmentDTO.getCode(),updatedDepartmentDTO.getStatus())).willReturn(null);
        given(departmentRepository.save(departmentToSave)).willReturn(departmentToSave);

        assertThat(departmentService.updateDepartment(updatedDepartmentDTO)).isEqualTo(departmentToSave);
        verify(departmentRepository).save(departmentToSave);

    }
}