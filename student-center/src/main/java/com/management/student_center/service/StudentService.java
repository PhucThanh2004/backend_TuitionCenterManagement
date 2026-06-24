package com.management.student_center.service;

import com.management.student_center.dto.*;
import com.management.student_center.dto.student.*;
import com.management.student_center.dto.student.StudentDTO;
import com.management.student_center.entity.*;
import com.management.student_center.enums.ActivityActionType;
import com.management.student_center.enums.ActivityTargetType;
import com.management.student_center.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

	private final StudentRepository studentRepository;
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	private final ParentContactRepository parentContactRepository;
	private final StudentSubjectRepository studentSubjectRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;
	private final StudentTuitionRepository studentTuitionRepository;
	private final StudentTuitionDetailRepository studentTuitionDetailRepository;
	private final ActivityLogService activityLogService;
	private final CurrentUserService currentUserService; // Thêm dependency này

	private static final Map<String, String> roleMapping = Map.of("R0", "Admin", "R1", "Giáo viên", "R2", "Học sinh");

	public StudentService(StudentRepository studentRepository, UserRepository userRepository,
			AddressRepository addressRepository, ParentContactRepository parentContactRepository,
			StudentSubjectRepository studentSubjectRepository, PasswordEncoder passwordEncoder,
			ImageService imageService, StudentTuitionDetailRepository studentTuitionDetailRepository,
			StudentTuitionRepository studentTuitionRepository, ActivityLogService activityLogService,
			CurrentUserService currentUserService) { // Thêm vào constructor
		this.studentRepository = studentRepository;
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
		this.parentContactRepository = parentContactRepository;
		this.studentSubjectRepository = studentSubjectRepository;
		this.passwordEncoder = passwordEncoder;
		this.imageService = imageService;
		this.studentTuitionDetailRepository = studentTuitionDetailRepository;
		this.studentTuitionRepository = studentTuitionRepository;
		this.activityLogService = activityLogService;
		this.currentUserService = currentUserService; // Khởi tạo
	}

	private Boolean parseStatus(String statusStr) {
		if (statusStr == null || statusStr.isEmpty())
			return null;
		return "true".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
	}

	public StudentGroupResponseDTO getAllStudentsGroupBySchool(Map<String, String> filters) {
		Boolean status = parseStatus(filters.get("status"));

		if (status == null) {
			status = true;
		}
		Specification<Student> spec = Specification.where(StudentSpecification.hasRole("R2"))
				.and(StudentSpecification.nameContains(filters.get("name")))
				.and(StudentSpecification.genderIs(parseGender(filters.get("gender"))))
				.and(StudentSpecification.gradeContains(filters.get("grade")))
				.and(StudentSpecification.schoolNameContains(filters.get("schoolName")))
				.and(StudentSpecification.hasStatus(status));
		List<Student> students = studentRepository.findAll(spec);

		long totalStudents = students.size();

		List<StudentDTO> studentDTOs = students.stream().map(this::mapToStudentDTO).toList();

		// GROUP THEO CẤP → TRƯỜNG

		Map<String, Map<String, Long>> totalByLevelAndSchool = new HashMap<>();
		Map<String, Map<String, List<StudentDTO>>> groupResult = new HashMap<>();

		for (StudentDTO s : studentDTOs) {

			String grade = s.getGrade();
			String level;

			if (grade == null) {
				level = "Chưa xác định";
			} else if (List.of("6", "7", "8", "9").contains(grade)) {
				level = "Cấp 2";
			} else if (List.of("10", "11", "12").contains(grade)) {
				level = "Cấp 3";
			} else {
				level = "Khác";
			}

			String school = (s.getSchoolName() != null && !s.getSchoolName().isBlank()) ? s.getSchoolName()
					: "Chưa có trường";

			groupResult.computeIfAbsent(level, k -> new HashMap<>()).computeIfAbsent(school, k -> new ArrayList<>())
					.add(s);

			totalByLevelAndSchool.computeIfAbsent(level, k -> new HashMap<>()).merge(school, 1L, Long::sum);
		}

		StudentGroupResponseDTO response = new StudentGroupResponseDTO();
		response.setTotalStudents(totalStudents);
		response.setStudentsBySchool(groupResult);
		response.setTotalStudentsBySchool(totalByLevelAndSchool);

		return response;
	}

	public List<StudentDTO> getLatestStudents() {
		List<Student> students = studentRepository.findTop5ByOrderByCreatedAtDesc();

		return students.stream().map(this::mapToStudentDTO).collect(Collectors.toList());
	}

	public PaginatedResponseDTO<StudentDTO> getAllStudents(int page, int limit, Map<String, String> filters) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		Boolean status = parseStatus(filters.get("status"));

		if (status == null) {
			status = true;
		}
		Specification<Student> spec = Specification.where(StudentSpecification.hasRole("R2"))
				.and(StudentSpecification.nameContains(filters.get("name")))
				.and(StudentSpecification.genderIs(parseGender(filters.get("gender"))))
				.and(StudentSpecification.gradeContains(filters.get("grade")))
				.and(StudentSpecification.schoolNameContains(filters.get("schoolName")))
				.and(StudentSpecification.hasSubjectName(filters.get("subject")))
				.and(StudentSpecification.hasStatus(status));
		;

		Page<Student> studentPage = studentRepository.findAll(spec, pageable);

		List<StudentDTO> studentDTOs = studentPage.getContent().stream().map(this::mapToStudentDTO)
				.collect(Collectors.toList());

		PaginationDTO pagination = new PaginationDTO(studentPage.getTotalElements(), page, limit,
				studentPage.getTotalPages());

		return new PaginatedResponseDTO<>(studentDTOs, pagination);
	}

	public StudentDTO getStudentById(Long userId) {
		Student student = studentRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên!"));
		return mapToStudentDTO(student);
	}

	@Transactional
	public User createNewStudent(CreateStudentDTO dto, MultipartFile file) {
		if (dto.getEmail() == null || dto.getFullName() == null || dto.getRoleId() == null) {
			throw new RuntimeException("Thiếu các thông tin bắt buộc.");
		}
		if (!"R2".equals(dto.getRoleId())) {
			throw new RuntimeException("RoleId phải là R2 (Học sinh).");
		}
		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email này đã tồn tại.");
		}

		String hashedPassword = passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "123456");
		String imagePath = (file != null && !file.isEmpty()) ? imageService.saveImage(file) : null;

		User newUser = new User();
		newUser.setEmail(dto.getEmail());
		newUser.setPassword(hashedPassword);
		newUser.setFullName(dto.getFullName());
		newUser.setPhoneNumber(dto.getPhoneNumber());
		newUser.setGender(dto.getGender());
		newUser.setImage(imagePath);
		newUser.setRoleId(dto.getRoleId());
		newUser.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
		User savedUser = userRepository.save(newUser);

		Address savedAddress = null;
		if (dto.getAddress() != null) {
			Address newAddress = new Address();
			newAddress.setDetails(dto.getAddress().getDetails());
			newAddress.setWard(dto.getAddress().getWard());
			newAddress.setProvince(dto.getAddress().getProvince());
			savedAddress = addressRepository.save(newAddress);
		}

		Student newStudent = new Student();
		newStudent.setUserInfo(savedUser);
		newStudent.setAddressInfo(savedAddress);
		newStudent.setDateOfBirth(dto.getDateOfBirth());
		newStudent.setGrade(dto.getGrade());
		newStudent.setSchoolName(dto.getSchoolName());
		Student savedStudent = studentRepository.save(newStudent);

		if (dto.getParents() != null && !dto.getParents().isEmpty()) {
			for (ParentContactDTO pDto : dto.getParents()) {
				ParentContact pc = new ParentContact();
				pc.setStudent(savedStudent);
				pc.setFullName(pDto.getFullName());
				pc.setPhoneNumber(pDto.getPhoneNumber());
				pc.setRelationship(pDto.getRelationship() != null ? pDto.getRelationship() : "Phụ huynh");
				parentContactRepository.save(pc);
			}
		}

		// LOG: Lấy current user từ service
		User currentUser = currentUserService.getCurrentUser();
		logStudentAction(
			currentUser,
			ActivityActionType.CREATE,
			savedStudent,
			"đã tạo học sinh mới: " + savedUser.getFullName()
		);

		return savedUser;
	}

	@Transactional
	public void updateAvatar(Long userId, MultipartFile file) {
		Student student = studentRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên!"));
		User user = student.getUserInfo();
		if (user.getImage() != null && !user.getImage().isEmpty()) {
			imageService.deleteImage(user.getImage());
		}
		String imagePath = imageService.saveImage(file);
		user.setImage(imagePath);
		userRepository.save(user);

		// LOG: Lấy current user từ service
		User currentUser = currentUserService.getCurrentUser();
		logStudentAction(
			currentUser,
			ActivityActionType.UPDATE,
			student,
			"đã cập nhật avatar của học sinh: " + user.getFullName()
		);
	}

	@Transactional
	public void updateStudent(Long userId, CreateStudentDTO dto, MultipartFile file) {
		Student student = studentRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên!"));
		User user = student.getUserInfo();
		Address address = student.getAddressInfo();

		String oldName = user.getFullName();
		String oldGrade = student.getGrade();
		String oldSchool = student.getSchoolName();

		user.setFullName(dto.getFullName());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.setGender(dto.getGender());
		if (dto.getStatus() != null) {
			user.setStatus(dto.getStatus());
		}

		if (file != null && !file.isEmpty()) {
			imageService.deleteImage(user.getImage());
			user.setImage(imageService.saveImage(file));
		}
		userRepository.save(user);

		student.setGrade(dto.getGrade());
		student.setSchoolName(dto.getSchoolName());
		student.setDateOfBirth(dto.getDateOfBirth());

		if (dto.getAddress() != null) {
			if (address == null) {
				address = new Address();
				student.setAddressInfo(address);
			}
			address.setDetails(dto.getAddress().getDetails());
			address.setWard(dto.getAddress().getWard());
			address.setProvince(dto.getAddress().getProvince());
			addressRepository.save(address);
		}
		studentRepository.save(student);

		parentContactRepository.deleteByStudentId(student.getId());
		if (dto.getParents() != null) {
			for (ParentContactDTO pDto : dto.getParents()) {
				ParentContact pc = new ParentContact();
				pc.setStudent(student);
				pc.setFullName(pDto.getFullName());
				pc.setPhoneNumber(pDto.getPhoneNumber());
				pc.setRelationship(pDto.getRelationship());
				parentContactRepository.save(pc);
			}
		}

		String description = "đã cập nhật thông tin học sinh: " + user.getFullName();
		if (!oldName.equals(user.getFullName())) {
			description += " (tên cũ: " + oldName + ")";
		}
		
		// LOG: Lấy current user từ service
		User currentUser = currentUserService.getCurrentUser();
		logStudentAction(
			currentUser,
			ActivityActionType.UPDATE,
			student,
			description
		);
	}

	/**
	 * deleteStudent
	 */
	@Transactional
	public void deleteStudent(Long userId) {

	    Student student = studentRepository.findByUserInfoId(userId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên!"));

	    User user = student.getUserInfo();

	    if (Boolean.FALSE.equals(user.getStatus())) {
	        throw new RuntimeException("Học viên đã bị ngưng hoạt động trước đó.");
	    }

	    user.setStatus(false);

	    userRepository.save(user);

	    User currentUser = currentUserService.getCurrentUser();
	    String description = "đã vô hiệu hóa học sinh: " + user.getFullName() + " (ID: " + userId + ")";
	    logStudentAction(currentUser, ActivityActionType.UPDATE, student, description);
	}

	/**
	 * Cập nhật nhiều học sinh
	 */
	@Transactional
	public void updateMultipleStudents(List<Long> userIds, CreateStudentDTO dto, MultipartFile file) {
		if (userIds == null || userIds.isEmpty()) {
			throw new RuntimeException("Danh sách ID trống!");
		}

		List<String> studentNames = new ArrayList<>();
		List<Long> studentIds = new ArrayList<>();

		for (Long id : userIds) {
			Student student = studentRepository.findByUserInfoId(id)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + id));
			User user = student.getUserInfo();
			studentNames.add(user.getFullName());
			studentIds.add(student.getId());

			// Cập nhật từng học sinh (gọi lại logic update hiện tại)
			updateStudent(id, dto, file);
		}

		// LOG CHUNG cho việc cập nhật nhiều học sinh
		User currentUser = currentUserService.getCurrentUser();
		String description = "đã cập nhật " + userIds.size() + " học sinh: " + String.join(", ", studentNames);
		String meta = """
				{
				    "action": "UPDATE_MULTIPLE",
				    "count": %d,
				    "userIds": %s,
				    "studentIds": %s,
				    "studentNames": "%s"
				}
				""".formatted(
				userIds.size(),
				userIds.toString(),
				studentIds.toString(),
				String.join(", ", studentNames)
		);

		activityLogService.log(
			currentUser,
			ActivityActionType.UPDATE,
			ActivityTargetType.STUDENT_LIST,
			null,
			description,
			meta
		);
	}

	/**
	 * Xóa nhiều học sinh - GHI LOG CHUNG
	 */
	@Transactional
	public void deleteMultipleStudents(List<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			throw new RuntimeException("Danh sách ID trống!");
		}
		
		List<String> studentNames = new ArrayList<>();
		List<Long> studentIds = new ArrayList<>();
		
		// Lấy thông tin trước khi xóa
		for (Long id : userIds) {
			Optional<Student> studentOpt = studentRepository.findByUserInfoId(id);
			if (studentOpt.isPresent()) {
				Student student = studentOpt.get();
				User user = student.getUserInfo();
				studentNames.add(user.getFullName());
				studentIds.add(student.getId());
			}
		}
		
		// LOG CHUNG trước khi xóa
		User currentUser = currentUserService.getCurrentUser();
		String description = "đã xóa " + userIds.size() + " học sinh: " + String.join(", ", studentNames);
		String meta = """
				{
				    "action": "DELETE_MULTIPLE",
				    "count": %d,
				    "userIds": %s,
				    "studentIds": %s,
				    "studentNames": "%s"
				}
				""".formatted(
				userIds.size(),
				userIds.toString(),
				studentIds.toString(),
				String.join(", ", studentNames)
		);

		activityLogService.log(
			currentUser,
			ActivityActionType.DELETE,
			ActivityTargetType.STUDENT_LIST,
			null,
			description,
			meta
		);
		
		// Thực hiện xóa từng học sinh
		for (Long id : userIds) {
			deleteStudent(id);
		}
	}

	public byte[] exportStudentsToExcel(Map<String, String> filters) throws IOException {
		Boolean gender = null;
		if (filters.get("gender") != null && !filters.get("gender").isBlank()) {
			gender = parseGender(filters.get("gender"));
		}
		Boolean status = parseStatus(filters.get("status"));

		if (status == null) {
			status = true;
		}
		Specification<Student> spec = Specification.where(StudentSpecification.hasRole("R2"))
				.and(StudentSpecification.nameContains(filters.get("name"))).and(StudentSpecification.genderIs(gender))
				.and(StudentSpecification.gradeContains(filters.get("grade")))
				.and(StudentSpecification.schoolNameContains(filters.get("schoolName")))
				.and(StudentSpecification.hasStatus(status));

		List<Student> students = studentRepository.findAll(spec);

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Danh sách học viên");

		Row headerRow = sheet.createRow(0);
		String[] headers = { "STT", "Họ và tên", "Email", "Giới tính", "Ngày sinh", "SĐT", "Khối", "Trường", "Địa chỉ",
				"Phụ huynh", "SĐT phụ huynh" };

		CellStyle headerStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.WHITE.getIndex());
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);

		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle);
		}

		int rowNum = 1;
		for (int i = 0; i < students.size(); i++) {
			Student s = students.get(i);
			User u = s.getUserInfo();
			if (u == null)
				continue;
			Address a = s.getAddressInfo();
			Row row = sheet.createRow(rowNum++);

			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(u.getFullName());
			row.createCell(2).setCellValue(u.getEmail());
			row.createCell(3).setCellValue(Boolean.TRUE.equals(u.getGender()) ? "Nam" : "Nữ");
			if (s.getDateOfBirth() != null) {
				row.createCell(4).setCellValue(s.getDateOfBirth().toString());
			} else {
				row.createCell(4).setCellValue("");
			}
			row.createCell(5).setCellValue(u.getPhoneNumber());
			row.createCell(6).setCellValue(s.getGrade());
			row.createCell(7).setCellValue(s.getSchoolName());

			String addressStr = "";
			if (a != null) {
				addressStr = String.join(", ", Optional.ofNullable(a.getDetails()).orElse(""),
						Optional.ofNullable(a.getWard()).orElse(""), Optional.ofNullable(a.getProvince()).orElse(""))
						.replaceAll("^, |^, |, $", "");
			}
			row.createCell(8).setCellValue(addressStr);

			String parentNameStr = "";
			String parentPhoneStr = "";

			if (s.getParentContacts() != null && !s.getParentContacts().isEmpty()) {
				parentNameStr = s.getParentContacts().stream().map(ParentContact::getFullName).filter(Objects::nonNull)
						.collect(Collectors.joining(", "));
				parentPhoneStr = s.getParentContacts().stream().map(ParentContact::getPhoneNumber)
						.filter(Objects::nonNull).collect(Collectors.joining(", "));
			}

			row.createCell(9).setCellValue(parentNameStr);
			row.createCell(10).setCellValue(parentPhoneStr);
		}

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();
		
		// LOG: Lấy current user và ghi log export
		User currentUser = currentUserService.getCurrentUser();
		String description = "đã xuất danh sách học sinh ra Excel với " + students.size() + " bản ghi";
		String meta = """
				{
				    "action": "EXPORT_EXCEL",
				    "totalRecords": %d,
				    "filters": %s
				}
				""".formatted(
				students.size(),
				filters != null ? filters.toString() : "{}"
		);

		activityLogService.log(
			currentUser,
			ActivityActionType.VIEW,
			ActivityTargetType.STUDENT_LIST,
			null,
			description,
			meta
		);
		
		return out.toByteArray();
	}

	private StudentDTO mapToStudentDTO(Student student) {
		User user = student.getUserInfo();
		Address address = student.getAddressInfo();

		AddressDTO addressDTO = new AddressDTO();
		if (address != null) {
			addressDTO.setId(address.getId());
			addressDTO.setDetails(address.getDetails());
			addressDTO.setWard(address.getWard());
			addressDTO.setProvince(address.getProvince());
		}
		List<StudentSubject> studentSubjects = studentSubjectRepository.findByStudentId(student.getId());

		List<StudentDTO.SubjectInfoDTO> subjectDTOs = studentSubjects.stream()
				.map(ss -> new StudentDTO.SubjectInfoDTO(ss.getSubject().getId(), ss.getSubject().getName()))
				.collect(Collectors.toList());

		List<ParentContactDTO> parentDTOs = student.getParentContacts().stream().map(p -> {
			ParentContactDTO dto = new ParentContactDTO();
			dto.setId(p.getId());
			dto.setFullName(p.getFullName());
			dto.setPhoneNumber(p.getPhoneNumber());
			dto.setRelationship(p.getRelationship());
			return dto;
		}).collect(Collectors.toList());

		StudentDTO dto = new StudentDTO();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setFullName(user.getFullName());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setGender(user.getGender());
		dto.setImage(user.getImage());
		dto.setRoleId(user.getRoleId());
		dto.setRoleName(roleMapping.getOrDefault(user.getRoleId(), ""));
		dto.setDateOfBirth(student.getDateOfBirth());
		dto.setGrade(student.getGrade());
		dto.setSchoolName(student.getSchoolName());
		dto.setAddress(addressDTO);
		dto.setParents(parentDTOs);
		dto.setSubjects(subjectDTOs);
		dto.setStatus(user.getStatus());
		dto.setCreatedAt(student.getCreatedAt());
		dto.setUpdatedAt(student.getUpdatedAt());

		return dto;
	}

	private Boolean parseGender(String genderStr) {
		if (genderStr == null || genderStr.isEmpty())
			return null;
		return "true".equalsIgnoreCase(genderStr) || "1".equals(genderStr);
	}

	public StudentStatisticDTO getStudentStatistics() {
		long totalStudents = studentRepository.count();
		YearMonth currentMonth = YearMonth.now();
		LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
		long newStudentsThisMonth = studentRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
		double percentageIncrease = 0;
		if (totalStudents > 0) {
			percentageIncrease = ((double) newStudentsThisMonth / totalStudents) * 100;
		}
		percentageIncrease = Math.round(percentageIncrease * 100.0) / 100.0;

		return new StudentStatisticDTO(totalStudents, newStudentsThisMonth, percentageIncrease);
	}
	
	@Transactional
	public void restoreStudent(Long userId) {

		Student student = studentRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy học viên!"));

		User user = student.getUserInfo();

		if (Boolean.TRUE.equals(user.getStatus())) {
			throw new RuntimeException("Học viên đang hoạt động.");
		}

		user.setStatus(true);

		userRepository.save(user);
	}

	public List<String> getDistinctSchools() {
		return studentRepository.findDistinctSchoolNames();
	}

	// =========================
	// HELPER LOG METHODS
	// =========================

	/**
	 * Ghi log cho hành động trên học sinh
	 * @param user Người thực hiện (đã được lấy từ CurrentUserService)
	 * @param actionType Loại hành động
	 * @param student Đối tượng học sinh
	 * @param description Mô tả hành động
	 */
	private void logStudentAction(
			User user,
			ActivityActionType actionType,
			Student student,
			String description
	) {
		String meta = """
				{
				    "studentId": %d,
				    "fullName": "%s",
				    "grade": "%s",
				    "schoolName": "%s",
				    "email": "%s"
				}
				""".formatted(
				student.getId(),
				student.getUserInfo().getFullName(),
				student.getGrade() != null ? student.getGrade() : "null",
				student.getSchoolName() != null ? student.getSchoolName() : "null",
				student.getUserInfo().getEmail()
		);

		activityLogService.log(
				user,
				actionType,
				ActivityTargetType.STUDENT_LIST,
				student.getId(), 
				description,
				meta
		);
	}
}