function patientRecordRow(record) {
  return `
    <tr>
      <td>${record.date}</td>
      <td>${record.diagnosis}</td>
      <td>${record.prescription}</td>
    </tr>
  `;
}
