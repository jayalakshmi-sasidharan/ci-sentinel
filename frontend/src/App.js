import React, { useState, useEffect } from 'react';
import './App.css';

function StatusBadge({ conclusion }) {
  const styles = {
    success: { background: '#22c55e', color: 'white' },
    failure: { background: '#ef4444', color: 'white' },
    in_progress: { background: '#f59e0b', color: 'white' },
    cancelled: { background: '#6b7280', color: 'white' },
  };

  const style = styles[conclusion] || styles.in_progress;

  return (
    <span style={{
      ...style,
      padding: '4px 10px',
      borderRadius: '12px',
      fontSize: '12px',
      fontWeight: '600',
      textTransform: 'uppercase'
    }}>
      {conclusion || 'in progress'}
    </span>
  );
}

function PipelineRow({ run }) {
  const date = new Date(run.createdAt).toLocaleString();
  const duration = run.durationSeconds > 0
    ? `${run.durationSeconds}s`
    : '—';

  return (
    <tr style={{ borderBottom: '1px solid #e5e7eb' }}>
      <td style={td}>{run.name}</td>
      <td style={td}><StatusBadge conclusion={run.conclusion} /></td>
      <td style={td}><code style={{ fontSize: '12px' }}>{run.branch}</code></td>
      <td style={td} title={run.commitMessage}>
        {run.commitMessage?.slice(0, 50)}{run.commitMessage?.length > 50 ? '...' : ''}
      </td>
      <td style={td}>{run.triggeredBy}</td>
      <td style={td}>{duration}</td>
      <td style={td}>{date}</td>
    </tr>
  );
}

const td = {
  padding: '12px 16px',
  fontSize: '14px',
  color: '#374151',
  verticalAlign: 'middle'
};

const th = {
  padding: '12px 16px',
  fontSize: '12px',
  fontWeight: '600',
  color: '#6b7280',
  textTransform: 'uppercase',
  letterSpacing: '0.05em',
  background: '#f9fafb',
  textAlign: 'left'
};

function SummaryCard({ label, value, color }) {
  return (
    <div style={{
      background: 'white',
      border: '1px solid #e5e7eb',
      borderRadius: '8px',
      padding: '20px 24px',
      minWidth: '150px'
    }}>
      <div style={{ fontSize: '28px', fontWeight: '700', color }}>{value}</div>
      <div style={{ fontSize: '13px', color: '#6b7280', marginTop: '4px' }}>{label}</div>
    </div>
  );
}

function App() {
  const [pipelines, setPipelines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('http://localhost:8080/api/pipelines')
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch pipelines');
        return res.json();
      })
      .then(data => {
        setPipelines(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const total = pipelines.length;
  const successful = pipelines.filter(p => p.conclusion === 'success').length;
  const failed = pipelines.filter(p => p.conclusion === 'failure').length;
  const successRate = total > 0 ? Math.round((successful / total) * 100) : 0;

  return (
    <div style={{ minHeight: '100vh', background: '#f3f4f6', fontFamily: 'Inter, sans-serif' }}>

      {/* Header */}
      <div style={{ background: '#111827', padding: '16px 32px', display: 'flex', alignItems: 'center', gap: '12px' }}>
        <span style={{ fontSize: '20px' }}>🛡️</span>
        <span style={{ color: 'white', fontWeight: '700', fontSize: '18px' }}>CI Sentinel</span>
        <span style={{ color: '#6b7280', fontSize: '14px', marginLeft: '8px' }}>Pipeline Observability Dashboard</span>
      </div>

      <div style={{ padding: '32px' }}>

        {/* Summary Cards */}
        <div style={{ display: 'flex', gap: '16px', marginBottom: '32px', flexWrap: 'wrap' }}>
          <SummaryCard label="Total Runs" value={total} color="#111827" />
          <SummaryCard label="Successful" value={successful} color="#22c55e" />
          <SummaryCard label="Failed" value={failed} color="#ef4444" />
          <SummaryCard label="Success Rate" value={`${successRate}%`} color="#3b82f6" />
        </div>

        {/* Table */}
        <div style={{ background: 'white', borderRadius: '8px', border: '1px solid #e5e7eb', overflow: 'hidden' }}>
          <div style={{ padding: '16px 20px', borderBottom: '1px solid #e5e7eb' }}>
            <h2 style={{ margin: 0, fontSize: '16px', fontWeight: '600', color: '#111827' }}>
              Pipeline Runs
            </h2>
          </div>

          {loading && (
            <div style={{ padding: '40px', textAlign: 'center', color: '#6b7280' }}>
              Loading pipeline data...
            </div>
          )}

          {error && (
            <div style={{ padding: '40px', textAlign: 'center', color: '#ef4444' }}>
              Error: {error} — make sure your backend is running on port 8080
            </div>
          )}

          {!loading && !error && (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={th}>Pipeline</th>
                  <th style={th}>Status</th>
                  <th style={th}>Branch</th>
                  <th style={th}>Commit</th>
                  <th style={th}>Triggered By</th>
                  <th style={th}>Duration</th>
                  <th style={th}>Started</th>
                </tr>
              </thead>
              <tbody>
                {pipelines.map(run => (
                  <PipelineRow key={run.id} run={run} />
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;