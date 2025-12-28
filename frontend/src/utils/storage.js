const STORAGE_KEYS = {
  AUTH: 'assessai_auth',
  REPORT: 'assessai_report',
};

export const storage = {
  getAuth: () => {
    const data = localStorage.getItem(STORAGE_KEYS.AUTH);
    return data ? JSON.parse(data) : null;
  },
  
  setAuth: (auth) => {
    localStorage.setItem(STORAGE_KEYS.AUTH, JSON.stringify(auth));
  },
  
  clearAuth: () => {
    localStorage.removeItem(STORAGE_KEYS.AUTH);
  },
  
  getReport: () => {
    const data = localStorage.getItem(STORAGE_KEYS.REPORT);
    return data ? JSON.parse(data) : [];
  },
  
  addToReport: (entry) => {
    const report = storage.getReport();
    report.push({
      ...entry,
      date: new Date().toISOString(),
      id: Date.now().toString(),
    });
    localStorage.setItem(STORAGE_KEYS.REPORT, JSON.stringify(report));
    return report;
  },
  
  getReportByStudent: (nom, prenom, email) => {
    const report = storage.getReport();
    return report.find(entry => 
      (email && entry.email === email) ||
      (entry.nom === nom && entry.prenom === prenom)
    );
  },
  
  clearReport: () => {
    localStorage.removeItem(STORAGE_KEYS.REPORT);
  },
};

