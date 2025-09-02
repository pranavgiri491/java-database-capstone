document.getElementById("prescriptionForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const prescription = {
    patientId: document.getElementById("patientId").value,
    medicine: document.getElementById("medicine").value,
    dosage: document.getElementById("dosage").value,
    instructions: document.getElementById("instructions").value,
  };

  try {
    await addPrescription(prescription);
    showModal("Prescription added successfully!");
    e.target.reset();
  } catch (err) {
    showModal("Error adding prescription.");
  }
});
