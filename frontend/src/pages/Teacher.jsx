import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { ocrService, nlpService, scoringService, feedbackService } from '../services/api';
import { storage } from '../utils/storage';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Teacher() {
  const { user, logout } = useAuth();
  const [referenceText, setReferenceText] = useState('');
  const [nom, setNom] = useState('');
  const [prenom, setPrenom] = useState('');
  const [studentId, setStudentId] = useState('');
  const [file, setFile] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState('');
  const [results, setResults] = useState([]);
  const [report, setReport] = useState([]);

  useEffect(() => {
    setReport(storage.getReport());
  }, []);

  const generateStudentId = () => {
    return `STU${Date.now()}`;
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) {
      setFile(null);
      return;
    }

    if (selectedFile.type !== 'application/pdf') {
      setError('Seuls les fichiers PDF sont acceptés');
      setFile(null);
      e.target.value = '';
      return;
    }

    setFile(selectedFile);
    setError('');
  };

  const exportCSV = () => {
    const headers = ['Nom', 'Prénom', 'Score', 'Status', 'Date'];
    const rows = report.map(entry => [
      entry.nom || '',
      entry.prenom || '',
      entry.score || 0,
      entry.status || 'Traité',
      new Date(entry.date).toLocaleDateString('fr-FR'),
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `rapport_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const processPipeline = async () => {
    if (!file) {
      setError('Veuillez sélectionner un fichier PDF');
      return;
    }

    if (!referenceText.trim()) {
      setError('Veuillez d\'abord enregistrer une référence');
      return;
    }

    if (!nom.trim() || !prenom.trim()) {
      setError('Veuillez remplir le nom et le prénom');
      return;
    }

    setLoading(true);
    setError('');
    setCurrentStep('OCR en cours...');

    try {
      const finalStudentId = studentId || generateStudentId();

      const ocrResult = await ocrService.process(file);
      const extractedText = ocrResult.extractedText || '';
      setCurrentStep('Analyse NLP en cours...');

      const nlpResult = await nlpService.analyze(extractedText);
      const cleanedText = nlpResult.cleanedText || extractedText;
      setCurrentStep('Évaluation en cours...');

      const scoringResult = await scoringService.evaluate(cleanedText, referenceText);
      const score = scoringResult.score || 0;
      const missingPoints = scoringResult.missingPoints || [];
      setCurrentStep('Génération du feedback...');

      const feedbackResult = await feedbackService.generate(
        finalStudentId,
        score,
        missingPoints,
        cleanedText,
        referenceText
      );
      const feedback = feedbackResult.feedback || '';

      const result = {
        nom,
        prenom,
        studentId: finalStudentId,
        extractedText: cleanedText.substring(0, 200) + (cleanedText.length > 200 ? '...' : ''),
        score,
        feedback,
        status: 'Traité',
      };

      setResults([result, ...results]);
      storage.addToReport({
        nom,
        prenom,
        studentId: finalStudentId,
        score,
        feedback,
        status: 'Traité',
        email: user.email,
      });
      setReport(storage.getReport());

      setNom('');
      setPrenom('');
      setStudentId('');
      setFile(null);
      const fileInput = document.querySelector('input[type="file"]');
      if (fileInput) fileInput.value = '';
    } catch (err) {
      setError(`Erreur à l'étape ${currentStep}: ${err.message}`);
    } finally {
      setLoading(false);
      setCurrentStep('');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
      {/* Modern Navbar */}
      <nav className="bg-white/80 backdrop-blur-lg shadow-lg border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-20">
            <div className="flex items-center space-x-3">
              <div className="p-2 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl">
                <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <h1 className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                AssessAI Enseignant
              </h1>
            </div>
            <div className="flex items-center gap-4">
              <div className="px-4 py-2 bg-gradient-to-r from-indigo-100 to-purple-100 rounded-xl">
                <span className="text-sm font-medium text-indigo-700">👤 {user?.email}</span>
              </div>
              <button
                onClick={logout}
                className="px-5 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-all duration-300 transform hover:scale-105 font-medium shadow-lg"
              >
                Déconnexion
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="space-y-8">
            {/* Reference Section */}
            <section className="bg-white/90 backdrop-blur-lg p-8 rounded-3xl shadow-xl border border-white/20">
              <div className="flex items-center gap-3 mb-6">
                <div className="p-3 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl">
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-gray-800">📚 Référence / Modèle</h2>
              </div>
              <textarea
                value={referenceText}
                onChange={(e) => setReferenceText(e.target.value)}
                rows="6"
                className="w-full px-5 py-4 bg-gray-50 border-2 border-gray-200 rounded-2xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300 resize-none"
                placeholder="Collez ici le texte de référence pour la correction..."
              />
              <button
                onClick={() => {
                  if (referenceText.trim()) {
                    alert('✅ Référence enregistrée avec succès!');
                  }
                }}
                className="mt-5 w-full px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-xl font-semibold hover:from-green-600 hover:to-emerald-700 transition-all duration-300 transform hover:scale-105 shadow-lg"
              >
                ✓ Enregistrer la référence
              </button>
            </section>

            {/* Scanner Section */}
            <section className="bg-white/90 backdrop-blur-lg p-8 rounded-3xl shadow-xl border border-white/20">
              <div className="flex items-center gap-3 mb-6">
                <div className="p-3 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl">
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-gray-800">📄 Scanner une copie</h2>
              </div>
              
              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    👤 Nom
                  </label>
                  <input
                    type="text"
                    value={nom}
                    onChange={(e) => setNom(e.target.value)}
                    className="w-full px-5 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300"
                    placeholder="Nom de l'étudiant"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    ✨ Prénom
                  </label>
                  <input
                    type="text"
                    value={prenom}
                    onChange={(e) => setPrenom(e.target.value)}
                    className="w-full px-5 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300"
                    placeholder="Prénom de l'étudiant"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    🆔 Student ID <span className="text-xs text-gray-500">(optionnel)</span>
                  </label>
                  <input
                    type="text"
                    value={studentId}
                    onChange={(e) => setStudentId(e.target.value)}
                    className="w-full px-5 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300"
                    placeholder="Généré automatiquement si vide"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    📎 Fichier PDF
                  </label>
                  <div className="relative">
                    <input
                      type="file"
                      accept=".pdf,application/pdf"
                      onChange={handleFileChange}
                      className="w-full px-5 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-gradient-to-r file:from-indigo-500 file:to-purple-600 file:text-white hover:file:from-indigo-600 hover:file:to-purple-700 file:cursor-pointer"
                    />
                  </div>
                  {file && (
                    <div className="mt-3 p-3 bg-green-50 border-2 border-green-200 rounded-xl flex items-center gap-2">
                      <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                      <p className="text-sm text-green-700 font-medium">{file.name}</p>
                    </div>
                  )}
                </div>

                {error && (
                  <div className="p-4 bg-red-50 border-2 border-red-200 rounded-xl text-red-700 animate-pulse">
                    <div className="flex items-center gap-2">
                      <span className="text-xl">⚠️</span>
                      <p className="font-medium">{error}</p>
                    </div>
                  </div>
                )}

                <button
                  onClick={processPipeline}
                  disabled={loading}
                  className="w-full px-6 py-4 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl font-semibold hover:from-indigo-700 hover:to-purple-700 disabled:from-gray-400 disabled:to-gray-400 disabled:cursor-not-allowed transition-all duration-300 transform hover:scale-105 shadow-lg"
                >
                  {loading ? (
                    <span className="flex items-center justify-center gap-2">
                      <LoadingSpinner />
                      <span>{currentStep}</span>
                    </span>
                  ) : (
                    <span className="flex items-center justify-center gap-2">
                      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
                      </svg>
                      Scanner & Évaluer
                    </span>
                  )}
                </button>
              </div>
            </section>

            {/* Recent Results */}
            {results.length > 0 && (
              <section className="bg-white/90 backdrop-blur-lg p-8 rounded-3xl shadow-xl border border-white/20">
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-3 bg-gradient-to-br from-purple-500 to-pink-600 rounded-xl">
                    <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
                    </svg>
                  </div>
                  <h2 className="text-2xl font-bold text-gray-800">🎯 Résultats récents</h2>
                </div>
                <div className="space-y-4">
                  {results.slice(0, 3).map((result, idx) => (
                    <div key={idx} className="border-2 border-indigo-100 rounded-2xl p-5 bg-gradient-to-br from-white to-indigo-50 hover:shadow-lg transition-all duration-300">
                      <div className="flex justify-between items-start mb-3">
                        <div>
                          <h3 className="font-bold text-lg text-gray-800">{result.prenom} {result.nom}</h3>
                          <p className="text-sm text-gray-600">🆔 {result.studentId}</p>
                        </div>
                        <div className="px-5 py-2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white font-bold rounded-xl text-xl shadow-lg">
                          {result.score}/20
                        </div>
                      </div>
                      <p className="text-sm text-gray-700 mb-3 bg-white/60 p-3 rounded-xl">{result.extractedText}</p>
                      <p className="text-sm text-gray-600 bg-indigo-50 p-3 rounded-xl">{result.feedback}</p>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </div>

          <div>
            {/* Report Table */}
            <section className="bg-white/90 backdrop-blur-lg p-8 rounded-3xl shadow-xl border border-white/20">
              <div className="flex justify-between items-center mb-6">
                <div className="flex items-center gap-3">
                  <div className="p-3 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-xl">
                    <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                    </svg>
                  </div>
                  <h2 className="text-2xl font-bold text-gray-800">📊 Rapport</h2>
                </div>
                <button
                  onClick={exportCSV}
                  className="px-5 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-xl hover:from-green-600 hover:to-emerald-700 text-sm font-semibold transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center gap-2"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M9 19l3 3m0 0l3-3m-3 3V10" />
                  </svg>
                  Exporter CSV
                </button>
              </div>

              <div className="overflow-x-auto rounded-2xl border-2 border-gray-200">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gradient-to-r from-indigo-500 to-purple-600">
                    <tr>
                      <th className="px-6 py-4 text-left text-xs font-bold text-white uppercase tracking-wider">Nom</th>
                      <th className="px-6 py-4 text-left text-xs font-bold text-white uppercase tracking-wider">Prénom</th>
                      <th className="px-6 py-4 text-left text-xs font-bold text-white uppercase tracking-wider">Score</th>
                      <th className="px-6 py-4 text-left text-xs font-bold text-white uppercase tracking-wider">Status</th>
                      <th className="px-6 py-4 text-left text-xs font-bold text-white uppercase tracking-wider">Date</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {report.length === 0 ? (
                      <tr>
                        <td colSpan="5" className="px-6 py-12 text-center text-gray-500">
                          <div className="flex flex-col items-center gap-3">
                            <div className="p-4 bg-gray-100 rounded-full">
                              <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                              </svg>
                            </div>
                            <p className="font-medium">Aucun résultat pour le moment</p>
                          </div>
                        </td>
                      </tr>
                    ) : (
                      report.map((entry, idx) => (
                        <tr key={idx} className="hover:bg-gradient-to-r hover:from-indigo-50 hover:to-purple-50 transition-all duration-200">
                          <td className="px-6 py-4 text-sm font-medium text-gray-900">{entry.nom}</td>
                          <td className="px-6 py-4 text-sm font-medium text-gray-900">{entry.prenom}</td>
                          <td className="px-6 py-4">
                            <span className="px-3 py-1 bg-gradient-to-r from-indigo-600 to-purple-600 text-white font-bold rounded-lg text-sm">
                              {entry.score}/20
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="px-3 py-1 bg-green-100 text-green-700 rounded-lg text-xs font-semibold">
                              ✓ {entry.status}
                            </span>
                          </td>
                          <td className="px-6 py-4 text-sm text-gray-600">
                            {new Date(entry.date).toLocaleDateString('fr-FR')}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}

