function patientRows(patient) {
  return `
    <tr>
      <td>${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.age}</td>
      <td>${patient.contact}</td>
    </tr>
  `;
}
