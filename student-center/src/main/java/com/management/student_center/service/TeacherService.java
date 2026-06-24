package com.management.student_center.service;

import com.management.student_center.dto.TeacherBasicDTO;
import com.management.student_center.entity.Teacher;
import com.management.student_center.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.management.student_center.dto.AddressDTO;
import com.management.student_center.dto.PaginationDTO;
import com.management.student_center.dto.PaginatedResponseDTO;
import com.management.student_center.dto.teacher.*;

import com.management.student_center.entity.*;
import com.management.student_center.enums.ActivityActionType;
import com.management.student_center.enums.ActivityTargetType;
import com.management.student_center.repository.*;

//Import Spring
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

//Import Java utils
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

//Import Apache POI (Excel)
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class TeacherService {

	private final TeacherRepository teacherRepository;
	private final UserRepository userRepository;
	private final AddressRepository addressRepository;
	private final StudentRepository studentRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;
	private static final Map<String, String> roleMapping = Map.of("R0", "Admin", "R1", "Giáo viên", "R2", "Học sinh");
	private final TeacherPaymentRepository teacherPaymentRepository;
	private final ActivityLogService activityLogService;
	private final CurrentUserService currentUserService;

	public TeacherService(TeacherRepository teacherRepository, UserRepository userRepository,
			AddressRepository addressRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder,
			ImageService imageService, TeacherPaymentRepository teacherPaymentRepository,
			ActivityLogService activityLogService, CurrentUserService currentUserService) {
		this.teacherRepository = teacherRepository;
		this.userRepository = userRepository;
		this.addressRepository = addressRepository;
		this.studentRepository = studentRepository;
		this.passwordEncoder = passwordEncoder;
		this.imageService = imageService;
		this.teacherPaymentRepository = teacherPaymentRepository;
		this.activityLogService = activityLogService;
		this.currentUserService = currentUserService;
	}

	private Boolean parseStatus(String statusStr) {
		if (statusStr == null || statusStr.isEmpty())
			return null;
		return "true".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
	}

	public PaginatedResponseDTO<TeacherDTO> getAllTeachers(int page, int limit, Map<String, String> filters) {
		Pageable pageable = PageRequest.of(page - 1, limit);

		Specification<Teacher> spec = Specification.where(TeacherSpecification.hasRole("R1"))
				.and(TeacherSpecification.nameContains(filters.get("name")))
				.and(TeacherSpecification.genderIs(parseGender(filters.get("gender"))))
				.and(TeacherSpecification.specialtyContains(filters.get("specialty")))
				.and(TeacherSpecification.hasStatus(parseStatus(filters.get("status"))));

		Page<Teacher> teacherPage = teacherRepository.findAll(spec, pageable);

		List<TeacherDTO> teacherDTOs = teacherPage.getContent().stream().map(this::mapToTeacherDTO)
				.collect(Collectors.toList());

		PaginationDTO pagination = new PaginationDTO(teacherPage.getTotalElements(), page, limit,
				teacherPage.getTotalPages());

		return new PaginatedResponseDTO<>(teacherDTOs, pagination);
	}

	public List<TeacherBasicDTO> getTeacherBasicList() {
		return teacherRepository.findAll().stream()
				.filter(t -> t.getUserInfo() != null && "R1".equals(t.getUserInfo().getRoleId())
						&& Boolean.TRUE.equals(t.getUserInfo().getStatus()))
				.map(t -> new TeacherBasicDTO(t.getId(), t.getUserInfo().getId(), t.getUserInfo().getFullName(),
						t.getUserInfo().getEmail(), t.getUserInfo().getPhoneNumber(), t.getUserInfo().getGender(),
						t.getSpecialty(), t.getUserInfo().getImage()))
				.collect(Collectors.toList());
	}

	@Transactional
	public User createNewEmployee(CreateEmployeeDTO dto, MultipartFile file) {
		if (dto.getEmail() == null || dto.getPassword() == null || dto.getFullName() == null
				|| dto.getRoleId() == null) {
			throw new RuntimeException("Thiếu các thông tin bắt buộc.");
		}
		if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
			throw new RuntimeException("Email này đã tồn tại trong hệ thống.");
		}

		String hashedPassword = passwordEncoder.encode(dto.getPassword());
		String imagePath = (file != null && !file.isEmpty()) ? imageService.saveImage(file) : null;

		User newUser = new User();
		newUser.setEmail(dto.getEmail());
		newUser.setPassword(hashedPassword);
		newUser.setFullName(dto.getFullName());
		newUser.setPhoneNumber(dto.getPhoneNumber());
		newUser.setGender(dto.getGender());
		newUser.setImage(imagePath);
		newUser.setRoleId(dto.getRoleId());
		newUser.setPasswordUpdatedAt(LocalDateTime.now());
		newUser.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
		User savedUser = userRepository.save(newUser);

		if ("R1".equals(dto.getRoleId())) {
			Address savedAddress = null;
			if (dto.getAddress() != null) {
				Address newAddress = new Address();
				newAddress.setDetails(dto.getAddress().getDetails());
				newAddress.setWard(dto.getAddress().getWard());
				newAddress.setProvince(dto.getAddress().getProvince());
				savedAddress = addressRepository.save(newAddress);
			}

			Teacher newTeacher = new Teacher();
			newTeacher.setUserInfo(savedUser);
			newTeacher.setAddressInfo(savedAddress);
			newTeacher.setDateOfBirth(dto.getDateOfBirth());
			newTeacher.setSpecialty(dto.getSpecialty());
			Teacher savedTeacher = teacherRepository.save(newTeacher);

			// LOG: Tạo giáo viên mới
			User currentUser = currentUserService.getCurrentUser();
			logTeacherAction(currentUser, ActivityActionType.CREATE, savedTeacher,
					"đã tạo giáo viên mới: " + savedUser.getFullName());
		}

		return savedUser;
	}

	/**
	 * updateEmployeeData
	 */
	@Transactional
	public Teacher updateEmployeeData(Long userId, UpdateEmployeeDTO dto, MultipartFile file) {
		Teacher teacher = teacherRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên!"));

		User user = teacher.getUserInfo();
		Address address = teacher.getAddressInfo();

		String oldName = user.getFullName();
		String oldSpecialty = teacher.getSpecialty();

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

		teacher.setDateOfBirth(dto.getDateOfBirth());
		teacher.setSpecialty(dto.getSpecialty());

		if (dto.getAddress() != null) {
			if (address == null) {
				address = new Address();
			}
			address.setDetails(dto.getAddress().getDetails());
			address.setWard(dto.getAddress().getWard());
			address.setProvince(dto.getAddress().getProvince());
			Address savedAddress = addressRepository.save(address);
			teacher.setAddressInfo(savedAddress);
		}

		Teacher updatedTeacher = teacherRepository.save(teacher);

		// LOG: Cập nhật giáo viên
		User currentUser = currentUserService.getCurrentUser();
		String description = "đã cập nhật thông tin giáo viên: " + user.getFullName();
		if (!oldName.equals(user.getFullName())) {
			description += " (tên cũ: " + oldName + ")";
		}
		if (!oldSpecialty.equals(teacher.getSpecialty()) && teacher.getSpecialty() != null) {
			description += " (chuyên môn cũ: " + oldSpecialty + ")";
		}

		logTeacherAction(currentUser, ActivityActionType.UPDATE, updatedTeacher, description);

		return updatedTeacher;
	}

	@Transactional
	public void restoreTeacher(Long userId) {

		Teacher teacher = teacherRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên!"));

		User user = teacher.getUserInfo();

		if (Boolean.TRUE.equals(user.getStatus())) {
			throw new RuntimeException("Giáo viên đang hoạt động.");
		}

		user.setStatus(true);

		userRepository.save(user);
	}

	@Transactional
	public void deleteEmployee(Long userId) {
	    Teacher teacher = teacherRepository.findByUserInfoId(userId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên!"));

	    User user = teacher.getUserInfo();

	    if (Boolean.FALSE.equals(user.getStatus())) {
	        throw new RuntimeException("Giáo viên đã bị ngưng hoạt động trước đó.");
	    }

	    /*
	     * long unpaidCount =
	     * teacherPaymentRepository.countUnpaidByTeacher(teacher.getId());
	     * 
	     * if (unpaidCount > 0) { throw new
	     * RuntimeException("Không thể ngưng hoạt động giáo viên vì vẫn còn lương chưa thanh toán."
	     * ); }
	     */

	    user.setStatus(false);
	    userRepository.save(user);

	    // LOG: Vô hiệu hóa giáo viên
	    User currentUser = currentUserService.getCurrentUser();
	    String description = "đã vô hiệu hóa giáo viên: " + user.getFullName() + " (ID: " + userId + ")";
	    logTeacherAction(currentUser, ActivityActionType.UPDATE, teacher, description);
	}

	@Transactional
	public void deleteMultipleTeachers(List<Long> userIds) {
	    if (userIds == null || userIds.isEmpty()) {
	        throw new RuntimeException("Danh sách ID không hợp lệ!");
	    }
	    
	    List<Long> successfullyDeleted = new ArrayList<>();
	    List<String> failedDeletions = new ArrayList<>();
	    
	    for (Long id : userIds) {
	        try {
	            // Lấy thông tin teacher trước khi xóa để log
	            Teacher teacher = teacherRepository.findByUserInfoId(id).orElse(null);
	            if (teacher != null && Boolean.TRUE.equals(teacher.getUserInfo().getStatus())) {
	                this.deleteEmployee(id);
	                successfullyDeleted.add(id);
	            } else if (teacher != null) {
	                failedDeletions.add("ID " + id + " (đã ngưng hoạt động)");
	            } else {
	                failedDeletions.add("ID " + id + " (không tìm thấy)");
	            }
	        } catch (Exception e) {
	            failedDeletions.add("ID " + id + " (lỗi: " + e.getMessage() + ")");
	        }
	    }
	    
	    // LOG: Xóa nhiều giáo viên
	    User currentUser = currentUserService.getCurrentUser();
	    String description = "đã vô hiệu hóa " + successfullyDeleted.size() + " giáo viên";
	    if (!failedDeletions.isEmpty()) {
	        description += " (thất bại: " + String.join(", ", failedDeletions) + ")";
	    }
	    
	    String meta = """
	            {
	                "totalIds": %d,
	                "successCount": %d,
	                "failedCount": %d,
	                "failedDetails": "%s"
	            }
	            """.formatted(
	            userIds.size(),
	            successfullyDeleted.size(),
	            failedDeletions.size(),
	            String.join("; ", failedDeletions)
	    );
	    
	    activityLogService.log(
	        currentUser,
	        ActivityActionType.DELETE,
	        ActivityTargetType.TEACHER_LIST,
	        null,
	        description,
	        meta
	    );
	}
	/**
	 * exportTeachersToExcel
	 */
	public byte[] exportTeachersToExcel(Map<String, String> filters) throws IOException {
		Specification<Teacher> spec = Specification.where(TeacherSpecification.hasRole("R1"))
				.and(TeacherSpecification.nameContains(filters.get("name")))
				.and(TeacherSpecification.genderIs(parseGender(filters.get("gender"))))
				.and(TeacherSpecification.specialtyContains(filters.get("specialty")))
				.and(TeacherSpecification.hasStatus(parseStatus(filters.get("status"))));

		List<Teacher> teachers = teacherRepository.findAll(spec);

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Danh sách giáo viên");

		Row headerRow = sheet.createRow(0);
		String[] headers = { "ID", "Họ tên", "Email", "Số điện thoại", "Giới tính", "Ngày sinh", "Chuyên môn",
				"Địa chỉ" };
		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}

		int rowNum = 1;
		for (Teacher t : teachers) {
			Row row = sheet.createRow(rowNum++);
			User u = t.getUserInfo();
			Address a = t.getAddressInfo();

			row.createCell(0).setCellValue(u.getId());
			row.createCell(1).setCellValue(u.getFullName());
			row.createCell(2).setCellValue(u.getEmail());
			row.createCell(3).setCellValue(u.getPhoneNumber() != null ? u.getPhoneNumber() : "");
			row.createCell(4).setCellValue(u.getGender() ? "Nam" : "Nữ");
			row.createCell(5).setCellValue(t.getDateOfBirth() != null ? t.getDateOfBirth().toString() : "");
			row.createCell(6).setCellValue(t.getSpecialty() != null ? t.getSpecialty() : "");

			String addressStr = "";
			if (a != null) {
				addressStr = List
						.of(Optional.ofNullable(a.getDetails()).orElse(""), Optional.ofNullable(a.getWard()).orElse(""),
								Optional.ofNullable(a.getProvince()).orElse(""))
						.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(", "));
			}
			row.createCell(7).setCellValue(addressStr);
		}

		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();

		// LOG: Export danh sách giáo viên
		User currentUser = currentUserService.getCurrentUser();
		String description = "đã xuất danh sách giáo viên ra Excel với " + teachers.size() + " bản ghi";
		String meta = """
				{
				    "action": "EXPORT_EXCEL",
				    "totalRecords": %d,
				    "filters": %s
				}
				""".formatted(teachers.size(), filters != null ? filters.toString() : "{}");

		activityLogService.log(currentUser, ActivityActionType.VIEW, ActivityTargetType.TEACHER_LIST, null, description,
				meta);

		return out.toByteArray();
	}

	private TeacherDTO mapToTeacherDTO(Teacher teacher) {
		User user = teacher.getUserInfo();
		Address address = teacher.getAddressInfo();

		AddressDTO addressDTO = new AddressDTO();
		if (address != null) {
			addressDTO.setId(address.getId());
			addressDTO.setDetails(address.getDetails());
			addressDTO.setWard(address.getWard());
			addressDTO.setProvince(address.getProvince());
		}

		TeacherDTO dto = new TeacherDTO();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setFullName(user.getFullName());
		dto.setPhoneNumber(user.getPhoneNumber());
		dto.setGender(user.getGender());
		dto.setImage(user.getImage());
		dto.setRoleId(user.getRoleId());
		dto.setRoleName(roleMapping.getOrDefault(user.getRoleId(), ""));
		dto.setDateOfBirth(teacher.getDateOfBirth());
		dto.setSpecialty(teacher.getSpecialty());
		dto.setAddress(addressDTO);
		dto.setStatus(user.getStatus());

		return dto;
	}

	private Boolean parseGender(String genderStr) {
		if (genderStr == null || genderStr.isEmpty()) {
			return null;
		}
		return "true".equalsIgnoreCase(genderStr) || "1".equals(genderStr);
	}

	public TeacherStatisticsDTO getTeacherStatistics() {
		long totalTeachers = teacherRepository.count();

		YearMonth currentMonth = YearMonth.now();
		LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
		LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

		long newTeachersThisMonth = teacherRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

		double percentageIncreaseTeacher = 0;
		if (totalTeachers > 0) {
			percentageIncreaseTeacher = ((double) newTeachersThisMonth / totalTeachers) * 100;
		}
		percentageIncreaseTeacher = Math.round(percentageIncreaseTeacher * 100.0) / 100.0;
		return new TeacherStatisticsDTO(totalTeachers, newTeachersThisMonth, percentageIncreaseTeacher);
	}

	public Long getTeacherIdByUserId(Long userId) {
		Teacher teacher = teacherRepository.findByUserInfoId(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy teacher với userId: " + userId));
		return teacher.getId();
	}

	// =========================
	// HELPER LOG METHODS
	// =========================

	/**
	 * Ghi log cho hành động trên giáo viên
	 * 
	 * @param user        Người thực hiện (đã được lấy từ CurrentUserService)
	 * @param actionType  Loại hành động
	 * @param teacher     Đối tượng giáo viên
	 * @param description Mô tả hành động
	 */
	private void logTeacherAction(User user, ActivityActionType actionType, Teacher teacher, String description) {
		String meta = """
				{
				    "teacherId": %d,
				    "fullName": "%s",
				    "specialty": "%s",
				    "email": "%s",
				    "dateOfBirth": "%s"
				}
				""".formatted(teacher.getId(), teacher.getUserInfo().getFullName(),
				teacher.getSpecialty() != null ? teacher.getSpecialty() : "null", teacher.getUserInfo().getEmail(),
				teacher.getDateOfBirth() != null ? teacher.getDateOfBirth().toString() : "null");

		activityLogService.log(user, actionType, ActivityTargetType.TEACHER_LIST, teacher.getId(), description, meta);
	}
}