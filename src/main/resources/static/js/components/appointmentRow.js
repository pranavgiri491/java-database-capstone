function appointmentRow(appt) {
  return `
    <tr>
      <td>${appt.date}</td>
      <td>${appt.doctor}</td>
      <td>${appt.status}</td>
    </tr>
  `;
}
