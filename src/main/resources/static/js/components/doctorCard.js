function doctorCard(doctor) {
  return `
    <div class="card">
      <h3>Dr. ${doctor.name}</h3>
      <p>Specialization: ${doctor.specialization}</p>
      <p>Experience: ${doctor.experience} years</p>
    </div>
  `;
}
