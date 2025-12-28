import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const ocrService = {
  process: async (file) => {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      const response = await api.post('/ocr/process', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log('✅ OCR Response from BACKEND:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ OCR Backend error:', error.message);
      if (error.response?.status === 404 || error.code === 'ECONNREFUSED') {
        console.warn('⚠️  Using MOCK OCR data');
        return { extractedText: `[MOCK OCR] Contenu extrait du PDF: ${file.name}\nTexte simulé pour démonstration.` };
      }
      throw error;
    }
  },
};

export const nlpService = {
  analyze: async (text) => {
    try {
      const response = await api.post('/nlp/analyze', { text });
      console.log('✅ NLP Response from BACKEND:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ NLP Backend error:', error.message);
      if (error.response?.status === 404 || error.code === 'ECONNREFUSED') {
        console.warn('⚠️  Using MOCK NLP data');
        return { 
          cleanedText: text.trim(),
          keywords: ['mot-clé1', 'mot-clé2', 'analyse'],
          sentiment: 'neutral'
        };
      }
      throw error;
    }
  },
};

export const scoringService = {
  evaluate: async (studentText, referenceText) => {
    try {
      const response = await api.post('/scoring/evaluate', {
        studentText,
        referenceText,
      });
      console.log('✅ SCORING Response from BACKEND:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ SCORING Backend error:', error.message);
      if (error.response?.status === 404 || error.code === 'ECONNREFUSED') {
        console.warn('⚠️  Using MOCK SCORING data');
        const similarity = calculateMockScore(studentText, referenceText);
        const score = Math.round(similarity * 20);
        return {
          score: Math.max(0, Math.min(20, score)),
          missingPoints: similarity < 0.7 ? ['Complétude', 'Précision'] : [],
        };
      }
      throw error;
    }
  },
};

export const feedbackService = {
  generate: async (studentId, score, missingPoints, studentText, referenceText) => {
    try {
      const response = await api.post('/feedback/generate', {
        studentId,
        score,
        missingPoints,
        studentText,
        referenceText,
      });
      console.log('✅ FEEDBACK Response from BACKEND:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ FEEDBACK Backend error:', error.message);
      if (error.response?.status === 404 || error.code === 'ECONNREFUSED') {
        console.warn('⚠️  Using MOCK FEEDBACK data');
        return {
          feedback: `[MOCK FEEDBACK] Score: ${score}/20. ${score >= 16 ? 'Excellent travail!' : score >= 12 ? 'Bon travail avec quelques améliorations possibles.' : 'Des efforts supplémentaires sont nécessaires. Revoir les concepts clés.'}`,
        };
      }
      throw error;
    }
  },
};

function calculateMockScore(text1, text2) {
  const words1 = text1.toLowerCase().split(/\s+/);
  const words2 = text2.toLowerCase().split(/\s+/);
  const commonWords = words1.filter(w => words2.includes(w));
  return commonWords.length / Math.max(words1.length, words2.length, 1);
}

