package com.management.student_center.assistant.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.management.student_center.repository.CurriculumRepository;
import com.management.student_center.repository.MaterialRepository;
import com.management.student_center.repository.SubjectRepository;
import com.management.student_center.repository.SubjectScheduleRepository;
import com.management.student_center.repository.TeacherSubjectRepository;
import com.management.student_center.entity.Curriculum;
import com.management.student_center.entity.Material;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.SubjectSchedule;
import com.management.student_center.entity.TeacherSubject;
import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;

@Service
public class PublicKnowledgeService {
	private final SubjectRepository subjectRepository;
	private final TeacherSubjectRepository teacherSubjectRepository;
	private final SubjectScheduleRepository subjectScheduleRepository;
	private final CurriculumRepository curriculumRepository;
	private final MaterialRepository materialRepository;

	public PublicKnowledgeService(SubjectRepository subjectRepository,
			TeacherSubjectRepository teacherSubjectRepository, SubjectScheduleRepository subjectScheduleRepository,
			CurriculumRepository curriculumRepository, MaterialRepository materialRepository) {
		this.subjectRepository = subjectRepository;
		this.teacherSubjectRepository = teacherSubjectRepository;
		this.subjectScheduleRepository = subjectScheduleRepository;
		this.curriculumRepository = curriculumRepository;
		this.materialRepository = materialRepository;
	}

	public String buildContext() {
		List<Subject> subjects = subjectRepository.findAllPublicSubjects();
		List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findAllWithTeacherAndSubject();
		List<SubjectSchedule> schedules = subjectScheduleRepository.findAllWithSubject();
		List<Material> materials = materialRepository.findAllWithSubject();
		List<Curriculum> curriculums = curriculumRepository.findAll();
		Map<Long, List<TeacherSubject>> teacherMap = teacherSubjects.stream()
				.collect(Collectors.groupingBy(ts -> ts.getSubject().getId()));

		Map<Long, List<SubjectSchedule>> scheduleMap = schedules.stream()
				.collect(Collectors.groupingBy(s -> s.getSubject().getId()));

		// Map<Long, List<Curriculum>> curriculumMap = curriculums.stream()
		// .collect(Collectors.groupingBy(c -> c.getSubject().getId()));

		// Map<Long, List<Material>> materialMap = materials.stream()
		// .collect(Collectors.groupingBy(m -> m.getSubject().getId()));

		StringBuilder context = new StringBuilder();
		// =====================================
		// THÔNG TIN CHUNG
		// =====================================
		context.append("""
				============================
				THÔNG TIN TRUNG TÂM
				============================

				Tên trung tâm:
				EduManage Center

				Địa chỉ:
				TP Hồ Chí Minh

				Hotline:
				0931113867

				Email:
				lathanhbatri2020@gmail.com

				Thời gian hoạt động:
				08:00 - 21:00 từ Thứ 2 đến Chủ Nhật

				Đối tượng đào tạo:
				Học sinh từ lớp 6 đến lớp 12.

				Hình thức học:
				- Học trực tiếp tại trung tâm
				- Học online qua Zoom

				Chính sách học thử:
				Học sinh được học thử miễn phí 4 buổi.

				Chính sách học bù:
				Nếu nghỉ có phép, học sinh được hỗ trợ học bù hoặc nhận tài liệu bài học.

				Đánh giá định kỳ:
				Trung tâm thực hiện đánh giá định kỳ hàng tháng và gửi báo cáo cho phụ huynh.

				Quy trình đăng ký:
				1. Để lại thông tin tư vấn.
				2. Trung tâm liên hệ xác nhận nhu cầu.
				3. Kiểm tra trình độ đầu vào (nếu cần).
				4. Xếp lớp phù hợp.
				5. Hoàn tất đăng ký và đóng học phí.

				Chính sách hoàn học phí:
				Phụ huynh vui lòng liên hệ trung tâm để được tư vấn theo từng trường hợp cụ thể.

				Ưu đãi:
				- Giảm học phí khi đăng ký nhiều môn.
				- Ưu đãi cho anh chị em cùng học tại trung tâm.
				""");

		context.append("""
				============================
				KHÓA HỌC ĐANG MỞ
				============================
				""");
		for (Subject subject : subjects) {
			Long currentStudents = subjectRepository.countCurrentStudents(subject.getId());
			Integer maxStudents = subject.getMaxStudents() != null ? subject.getMaxStudents() : 0;

			int remainingSlots = Math.max(0, maxStudents - currentStudents.intValue());
			context.append("\n");
			context.append("====================================\n");
			context.append("KHÓA HỌC: ").append(subject.getName()).append("\n");
			context.append("====================================\n");

			context.append("Khối lớp: ").append(subject.getGrade()).append("\n");

			context.append("Buổi học mỗi tuần: ").append(subject.getSessionsPerWeek()).append(" buổi\n");

			context.append("Sĩ số tối đa: ").append(maxStudents).append(" học sinh\n");

			context.append("Sĩ số hiện tại: ").append(currentStudents).append("/").append(maxStudents)
					.append(" học sinh\n");

			context.append("Số chỗ còn lại: ").append(remainingSlots).append(" học sinh\n");

			if (currentStudents >= maxStudents) {
			    context.append("Tình trạng lớp: Lớp đã đủ sĩ số và tạm ngừng nhận thêm học sinh.\n");
			} else {
			    context.append("Tình trạng lớp: Hiện vẫn còn nhận thêm học sinh đăng ký mới.\n");
			}
			// =========================
			// HỌC PHÍ
			// =========================
			if (subject.getBillingType() == BillingType.PER_HOUR) {
				context.append("Hình thức tính học phí: Theo giờ học\n");
				context.append("Đơn giá: ").append(formatCurrency(subject.getPrice())).append(" VNĐ/giờ\n");

				context.append("Chu kỳ đóng học phí: Đóng học phí mỗi tháng một lần.\n");
			} else if (subject.getBillingType() == BillingType.PER_SUBJECT) {
				context.append("Hình thức tính học phí: Trọn khóa theo môn học\n");
				context.append("Tổng học phí khóa học: ").append(formatCurrency(subject.getPrice())).append(" VNĐ\n");
				if (subject.getPaymentPlanType() == PaymentPlanType.ONE_TIME) {
					context.append("Phương thức thanh toán: Thanh toán một lần cho toàn bộ khóa học.\n");
				} else if (subject.getPaymentPlanType() == PaymentPlanType.INSTALLMENT) {
					context.append("Phương thức thanh toán: Thanh toán theo nhiều đợt.\n");
					if (subject.getInstallmentCount() != null && subject.getInstallmentCount() > 0) {
						BigDecimal installmentAmount = subject.getPrice()
								.divide(BigDecimal.valueOf(subject.getInstallmentCount()), 0, RoundingMode.HALF_UP);
						context.append("Số đợt thanh toán: ").append(subject.getInstallmentCount()).append(" đợt\n");
						context.append("Mỗi đợt khoảng: ").append(formatCurrency(installmentAmount)).append(" VNĐ\n");
					}
				}
			}
			if (subject.getNote() != null) {
				context.append("Mô tả khóa học: ").append(subject.getNote()).append("\n");
			}
			context.append("\nGiáo viên giảng dạy:\n");

			teacherMap.getOrDefault(subject.getId(), List.of()).forEach(ts -> {

				context.append("- ").append(ts.getTeacher().getUserInfo().getFullName());

				if (ts.getTeacher().getSpecialty() != null) {
					context.append(" (").append(ts.getTeacher().getSpecialty()).append(")");
				}

				context.append("\n");
			});

			context.append("\nLịch học:\n");

			scheduleMap.getOrDefault(subject.getId(), List.of()).stream()
					.sorted((a, b) -> a.getDayOfWeek().compareTo(b.getDayOfWeek())).forEach(schedule -> {

						context.append("- ").append(convertDayOfWeek(schedule.getDayOfWeek())).append(": ")
								.append(schedule.getStartTime()).append(" - ").append(schedule.getEndTime())
								.append("\n");
					});

			/*
			 * context.append("\nGiáo trình:\n");
			 * 
			 * curriculumMap.getOrDefault(subject.getId(), List.of()).forEach(c -> {
			 * 
			 * context.append("- ").append(c.getTitle());
			 * 
			 * if (c.getExpectedSessions() != null) {
			 * context.append(" (").append(c.getExpectedSessions()).append(" buổi)"); }
			 * 
			 * context.append("\n"); });
			 * 
			 * context.append("\nTài liệu học tập:\n");
			 * 
			 * materialMap.getOrDefault(subject.getId(), List.of()).forEach(material -> {
			 * 
			 * context.append("- ").append(material.getTitle());
			 * 
			 * if (material.getType() != null) {
			 * context.append(" (").append(material.getType()).append(")"); }
			 * 
			 * context.append("\n"); });
			 * 
			 * context.append("\n");
			 */
		}
		return context.toString();
	}

	private String convertDayOfWeek(Integer day) {
		return switch (day) {
		case 0 -> "Chủ nhật";
		case 1 -> "Thứ 2";
		case 2 -> "Thứ 3";
		case 3 -> "Thứ 4";
		case 4 -> "Thứ 5";
		case 5 -> "Thứ 6";
		case 6 -> "Thứ 7";
		default -> "Không xác định";
		};
	}

	private String formatCurrency(BigDecimal amount) {
		return String.format("%,.0f", amount).replace(",", ".");
	}
}