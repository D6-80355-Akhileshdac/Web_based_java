package io.bytecode.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.bytecode.dao.AppointmentRepository;
import io.bytecode.dao.DoctorRepository;
import io.bytecode.dao.PatientRepository;
import io.bytecode.dto.AppointmentDto;
import io.bytecode.entites.Appointment;
import io.bytecode.entites.Doctor;
import io.bytecode.entites.Patient;
import io.bytecode.exception.AppointmentServiceException;

@Service
public class AppointmentService {
	
	@Autowired
	private AppointmentRepository rep;
	@Autowired
	private DoctorRepository docRep;
	@Autowired
	private PatientRepository patientRep;
	
	public void bookAppointment(AppointmentDto dto) {
		Appointment appointment = new Appointment();
		appointment.setId(dto.getId());
		appointment.setDate(LocalDate.parse(dto.getDate()));
		appointment.setTime(LocalTime.parse(dto.getTime()));
		Doctor doctor = docRep.findById(dto.getDoctorId()).orElseThrow();
		appointment.setDoctor(doctor);
		Patient patient = patientRep.findById(dto.getPatientId()).orElseThrow();
		appointment.setPatient(patient);
		List<Appointment> list = getAppointmentsByDoctor(dto.getDoctorId());
		for (Appointment a : list) {
			if (a.getDate().isEqual(appointment.getDate()) && a.getTime().equals(appointment.getTime()))
				throw new AppointmentServiceException("Conflicting Appointment");
		}
		rep.save(appointment);
	}
	
	public List<Appointment> getAppointmentsByDoctor(Integer id) {
		Doctor doctor = docRep.findById(id).orElseThrow(() -> new AppointmentServiceException("No Doctor found with ID " + id));
		return rep.findByDoctor(doctor);
	}
	
	public void cancelAppointment(Integer id) {
		rep.deleteById(id);
	}
}
