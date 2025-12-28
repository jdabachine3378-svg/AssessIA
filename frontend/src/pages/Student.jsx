import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { storage } from '../utils/storage';
import LoadingSpinner from '../components/LoadingSpinner';

export default function Student() {
  const { user, logout } = useAuth();
  const [nom, setNom] = useState('');
  const [prenom, setPrenom] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  const searchResult = () => {
    setLoading(true);
    setError('');
    setResult(null);

    setTimeout(() => {
      const report = storage.getReport();
      const found = storage.getReportByStudent(nom, prenom, user.email);
      
      if (found) {
        setResult(found);
      } else {
        setError('Aucun résultat trouvé. Vérifiez vos informations.');
      }
      
      setLoading(false);
    }, 500);
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
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                </svg>
              </div>
              <h1 className="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                AssessAI Étudiant
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

      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="bg-white/90 backdrop-blur-lg p-10 rounded-3xl shadow-2xl border border-white/20">
          {/* Header */}
          <div className="text-center mb-10">
            <div className="inline-block p-4 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl mb-4 shadow-xl">
              <svg className="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
              </svg>
            </div>
            <h2 className="text-3xl font-bold text-gray-800 mb-2">Consulter mes résultats</h2>
            <p className="text-gray-600">Entrez vos informations pour accéder à vos évaluations</p>
          </div>

          {/* Search Form */}
          <div className="space-y-5 mb-8">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                📝 Nom
              </label>
              <input
                type="text"
                value={nom}
                onChange={(e) => setNom(e.target.value)}
                className="w-full px-5 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all duration-300 hover:border-purple-300"
                placeholder="Votre nom"
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
                placeholder="Votre prénom"
              />
            </div>

            <button
              onClick={searchResult}
              disabled={loading || !nom.trim() || !prenom.trim()}
              className="w-full px-5 py-4 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-xl font-semibold hover:from-indigo-700 hover:to-purple-700 disabled:from-gray-400 disabled:to-gray-400 disabled:cursor-not-allowed transition-all duration-300 transform hover:scale-105 shadow-lg"
            >
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <LoadingSpinner />
                  Recherche en cours...
                </span>
              ) : (
                <span className="flex items-center justify-center gap-2">
                  🔍 Rechercher mes résultats
                </span>
              )}
            </button>
          </div>

          {/* Error Message */}
          {error && (
            <div className="p-5 bg-red-50 border-2 border-red-200 rounded-xl text-red-700 mb-6 animate-pulse">
              <div className="flex items-center gap-3">
                <span className="text-2xl">⚠️</span>
                <p className="font-medium">{error}</p>
              </div>
            </div>
          )}

          {/* Result Display */}
          {result && (
            <div className="mt-8 p-8 bg-gradient-to-br from-indigo-50 via-purple-50 to-pink-50 rounded-2xl border-2 border-indigo-200 shadow-xl animate-fadeIn">
              <div className="text-center mb-8">
                <div className="inline-block relative">
                  <div className="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-3xl blur-xl opacity-50 animate-pulse"></div>
                  <div className="relative bg-gradient-to-r from-indigo-600 to-purple-600 text-white text-6xl font-bold px-12 py-8 rounded-3xl shadow-2xl">
                    {result.score}<span className="text-3xl">/20</span>
                  </div>
                </div>
                <p className="text-gray-600 font-semibold mt-4 text-lg">🎯 Score obtenu</p>
              </div>

              <div className="bg-white/80 backdrop-blur-sm p-6 rounded-2xl shadow-lg">
                <div className="flex items-center gap-3 mb-4">
                  <span className="text-2xl">💬</span>
                  <h3 className="font-bold text-gray-800 text-xl">Feedback</h3>
                </div>
                <p className="text-gray-700 leading-relaxed">
                  {result.feedback || 'Votre copie a été évaluée avec succès. Continuez vos efforts! 🌟'}
                </p>
              </div>

              <div className="mt-6 text-center">
                <div className="inline-flex items-center gap-2 px-6 py-3 bg-white/60 backdrop-blur-sm rounded-xl text-gray-600">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span className="font-medium">
                    {new Date(result.date).toLocaleDateString('fr-FR', { 
                      weekday: 'long', 
                      year: 'numeric', 
                      month: 'long', 
                      day: 'numeric' 
                    })}
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Empty State */}
          {!result && !error && (
            <div className="text-center text-gray-500 mt-12 p-8">
              <div className="inline-block p-6 bg-gray-100 rounded-full mb-4">
                <svg className="w-16 h-16 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <p className="text-lg font-medium">Entrez vos informations pour consulter vos résultats 📊</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

