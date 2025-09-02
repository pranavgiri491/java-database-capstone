document.addEventListener("DOMContentLoaded", async () => {
  const appointments = await fetchAppointments();
  const tbody = document.getElementById("appointmentRows");
  tbody.innerHTML = appointments.map(appointmentRow).join("");
});
