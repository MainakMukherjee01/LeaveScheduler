import React from 'react';
import { Modal, Button } from 'react-bootstrap';

const ApproveRejectModal = ({ leave, onClose, onDecision }) => {
  if (!leave) return null;

  return (
    <Modal show onHide={onClose}>
      <Modal.Header closeButton>
        <Modal.Title>Leave Request Review</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p><strong>Employee:</strong> {leave.employeeName}</p>
        <p><strong>Leave Type:</strong> {leave.leaveType}</p>
        <p><strong>Duration:</strong> {leave.startDate} to {leave.endDate}</p>
        <p><strong>Reason:</strong> {leave.reason}</p>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="success" onClick={() => onDecision(leave.id, 'approve')}>
          Approve
        </Button>
        <Button variant="danger" onClick={() => onDecision(leave.id, 'reject')}>
          Reject
        </Button>
        <Button variant="secondary" onClick={onClose}>
          Cancel
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ApproveRejectModal;
