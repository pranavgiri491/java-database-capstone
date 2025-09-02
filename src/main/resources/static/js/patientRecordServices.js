document.addEventListener("DOMContentLoaded", async () => {
  const records = await fetchPatientRecords(1); // Example patientId=1
  const tbody = document.getElementById("patientRecordRows");
  tbody.innerHTML = records.map(patientRecordRow).join("");
});
