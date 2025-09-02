document.getElementById("updateForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("appointmentId").value;
  const data = {
    date: document.getElementById("date").value,
    status: document.getElementById("status").value,
  };

  try {
    await updateAppointment(id, data);
    showModal("Appointment updated successfully!");
  } catch (err) {
    showModal("Failed to update appointment.");
  }
});
