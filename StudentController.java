package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    AddressRepository addressRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page){
        Pageable pageable=PageRequest.of(page,10);
        Page<Student> StudentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return StudentPage;
    }

    //4. GROUP OWNER
    @GetMapping("/{groupId}")
    public Page<Student> getStudentListByGroupId(@PathVariable Integer groupId,
                                                 @RequestParam int page){
        Pageable pageable=PageRequest.of(page,10);
        return studentRepository.findAllByGroupId(groupId,pageable);
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto){
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectList());

            Address address=new Address();
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            Address savedAddress = addressRepository.save(address);

            Student student=new Student();
            student.setAddress(savedAddress);
            student.setGroup(group);
            student.setSubjects(subjectList);
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            studentRepository.save(student);
            return "Student added";
        }
        return "Group not found";
    }

    @DeleteMapping("/{id}")
    public String deleteSudent(@PathVariable Integer id){
        try {
            studentRepository.deleteById(id);
            return "Student deleted";
        }catch (Exception e){
            return "Exception in deleting";
        }
    }

    @PutMapping("/{id}")
    public String EditStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()) {
                Group group = optionalGroup.get();
                Student editingStudent = optionalStudent.get();

                editingStudent.setLastName(studentDto.getLastName());
                editingStudent.setFirstName(studentDto.getFirstName());

                Address address = editingStudent.getAddress();
                address.setStreet(studentDto.getStreet());
                address.setDistrict(studentDto.getDistrict());
                address.setCity(studentDto.getCity());
                addressRepository.save(address);

                List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectList());
                editingStudent.setSubjects(subjectList);
                editingStudent.setGroup(group);

                studentRepository.save(editingStudent);
                return "Student edited";
            }
            return "Group not found";
        }
        return "Student not found";
    }


}
