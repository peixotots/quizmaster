# Quiz Master 🏆

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Integrated-orange.svg)](https://firebase.google.com)
[![Room](https://img.shields.io/badge/Room-2.8.4-green.svg)](https://developer.android.com/training/data-storage/room)
[![MinSDK](https://img.shields.io/badge/MinSDK-24-brightgreen.svg)](https://developer.android.com/about/versions/nougat)

> Aplicativo Android de quiz desenvolvido em Kotlin com Jetpack Compose, oferecendo desafios de conhecimento em diversos temas com sistema de autenticação individual e sincronização de dados.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Configuração do Projeto](#configuração-do-projeto)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Principais Desafios Técnicos](#principais-desafios-técnicos)
- [Autores](#autores)

---

## 🎯 Sobre o Projeto

O **Quiz Master** é um aplicativo Android desenvolvido como projeto acadêmico que implementa um sistema completo de quizzes com autenticação individual, armazenamento local e sincronização em nuvem. O aplicativo permite que usuários criem e respondam quizzes de diferentes temas, acompanhem seu desempenho e visualizem estatísticas detalhadas.

### **Objetivo Acadêmico**
Demonstrar competências em:
- Desenvolvimento mobile Android nativo com Kotlin
- Integração com Firebase (Authentication & Cloud Firestore)
- Persistência de dados local com Room Database
- Interface moderna com Jetpack Compose
- Arquitetura MVVM e Clean Architecture
- Sincronização de dados entre banco local e nuvem

---

## ✨ Funcionalidades

### **1. Autenticação**

#### 🔐 **Sistema de Autenticação**
- **Cadastro de Usuário**
  - Coleta de nome completo, e-mail e senha
  - Confirmação de senha
  - Persistência automática no Firebase Firestore e Room Database

- **Login Seguro**
  - Autenticação via Firebase Authentication
  - Autenticação biométrica (digital/Face ID) opcional
  - Modo offline com fallback de credenciais armazenadas
  - Tratamento de erros amigável em português
  - Alternância de visibilidade de senha

- **Recuperação de Acesso**
  - Sistema "Esqueci minha senha"
  - Envio automático de e-mail de redefinição via Firebase

- **Perfil do Usuário**
  - Armazenamento no Firestore e Room com:
    - Nome completo
    - E-mail
    - Pontuação (score)
    - Quizzes concluídos
    - Avatar personalizado
    - Data de criação (createdAt)

### **2. Gestão de Questões**

#### 📥 **Download e Armazenamento Local**
- Download de questões do Firebase Cloud Firestore
- Armazenamento local com Room Database para uso offline completo
- Sincronização automática em tempo real quando houver atualizações
- Cache inteligente de dados com persistência do Firestore
- Fallback para cache local quando sem conexão

### **3. Execução de Quiz**

#### 🎮 **Sistema de Quiz Dinâmico**
- Apresentação de questões armazenadas localmente
- Interface interativa com swipe entre questões
- Indicador visual de progresso
- Feedback imediato de respostas corretas/incorretas
- Cálculo automático de pontuação (pontos por acerto)

#### 📊 **Exibição de Desempenho**
- Percentual de acertos
- Pontuação final
- Contagem de acertos e erros
- Salvamento automático de sessão:
  - **Local:** Room Database
  - **Nuvem:** Firebase Firestore

### **4. Histórico e Estatísticas**

#### 📈 **Acompanhamento de Progresso**
- Histórico completo de quizzes respondidos
- Estatísticas detalhadas no perfil:
  - Total de quizzes realizados
  - Taxa de acerto geral
  - Total de acertos e erros
  - Pontuação acumulada
  - Evolução de desempenho
- Comparação entre sessões
- Gráficos visuais de desempenho

### **5. Interface e Experiência**

#### 🎨 **Design Premium**
- Tela de login com autenticação biométrica
- Tela de cadastro com validações
- Tela inicial (Home) com:
  - Lista de quizzes disponíveis
  - Quizzes concluídos
  - Rascunhos de quizzes criados
- Tela de perfil com estatísticas completas
- Tela de ranking global de usuários
- Interface responsiva com Material Design 3

### **🎁 Funcionalidades Extras**
- **Criação de Quizzes:** Usuários podem criar seus próprios quizzes
- **Sistema de Rascunhos:** Salvar quizzes como rascunho antes de publicar
- **Ranking Global:** Visualização dos melhores jogadores
- **Avatares Personalizados:** Escolha de emoji como avatar
- **Edição de Perfil:** Alteração de nome e avatar
- **Login Biométrico:** Acesso rápido com digital ou Face ID

---

## 🛠️ Tecnologias Utilizadas

### **Frontend**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Kotlin** | 1.9+ | Linguagem principal |
| **Jetpack Compose** | Latest | Framework UI declarativo |
| **Material Design 3** | Latest | Sistema de design |
| **Compose Navigation** | - | Navegação entre telas |

### **Backend & Cloud**
| Tecnologia | Descrição |
|-----------|-----------|
| **Firebase Authentication** | Autenticação de usuários |
| **Cloud Firestore** | Banco de dados NoSQL em nuvem |
| **Firestore Offline Persistence** | Cache local automático |

### **Persistência Local**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Room Database** | 2.8.4 | ORM para SQLite |
| **Room KTX** | 2.8.4 | Extensões Kotlin para Room |
| **SharedPreferences** | - | Armazenamento de preferências |

### **Ferramentas de Build**
- **Gradle KTS** (Kotlin DSL)
- **KSP** (Kotlin Symbol Processing)
- **Google Services Plugin**

---

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **MVVM**:

```
app/
└── src/main/java/com/example/quizandroid/
    ├── data/                      # Camada de Dados
    │   ├── local/                 # Room Database
    │   │   ├── dao/              # Data Access Objects
    │   │   ├── entities/         # Entidades do banco local
    │   │   └── AppDatabase.kt    # Configuração do Room
    │   ├── remote/                # Firebase
    │   │   └── QuizRepository.kt # Repositório Firebase
    │   └── model/                 # Modelos de dados
    │       ├── UserEntity.kt
    │       ├── Models.kt
    │       └── UserPrefsManager.kt
    │
    ├── ui/                        # Camada de Apresentação (Compose)
    │   ├── login/                # Login, Cadastro, Home
    │   ├── PlayQuizScreen.kt     # Execução de quiz
    │   ├── ProfileScreen.kt      # Perfil e estatísticas
    │   ├── RankingScreen.kt      # Ranking global
    │   ├── CreateQuestionsScreen.kt # Criação de quiz
    │   └── theme/                # Tema e cores
    │
    ├── MainActivity.kt            # Activity principal
    └── FirebaseErrorHelper.kt     # Tradutor de erros
```

### **Fluxo de Dados**
```
UI (Compose) → Repository → Data Source (Room/Firebase)
                   ↓
              State/Flow
                   ↓
            UI Atualização
```

### **Estratégia de Sincronização**
O aplicativo implementa uma estratégia **dual-database** (Room + Firestore):

1. **Online:** Dados são salvos em **ambos** os bancos simultaneamente
2. **Offline:** Dados são salvos no Room e sincronizados quando a conexão retornar
3. **Leitura:** Tenta buscar do Firestore primeiro, faz fallback para Room/Cache se falhar
4. **Listeners em Tempo Real:** Atualizações automáticas via Firestore SnapshotListeners

---

## ⚙️ Configuração do Projeto

### **Pré-requisitos**
- Android Studio | 2023.1.1 ou superior
- JDK 17 ou superior
- Conta no Firebase
- Dispositivo Android ou Emulador (API 24+)

## 📁 Estrutura de Pastas

```
quizmaster/
├── .idea/                    # Configurações do Android Studio
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/quizandroid/
│   │   │   │   ├── data/           # Repositórios e fontes de dados
│   │   │   │   ├── ui/             # Interface do usuário (Compose)
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── FirebaseErrorHelper.kt
│   │   │   ├── res/                # Recursos (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                   # Testes unitários (opcional)
│   ├── build.gradle.kts            # Dependências do módulo app
│   └── google-services.json        # Configuração Firebase (NÃO COMMITADO)
├── gradle/                         # Wrapper do Gradle
├── build.gradle.kts                # Configuração Gradle raiz
├── settings.gradle.kts             # Configuração de módulos
├── gradle.properties               # Propriedades do projeto
├── .gitignore                      # Arquivos ignorados pelo Git
└── README.md                       # Este arquivo
```

---

## 🚧 Principais Desafios Técnicos

Durante o desenvolvimento deste projeto, enfrentamos diversos desafios técnicos importantes:

### **Desafio 1️⃣: Sincronização Offline e Integração de Dois Bancos**
**Descrição do Problema:**
O maior desafio foi implementar um sistema robusto de **armazenamento offline** que funcionasse em harmonia com o banco de dados na nuvem (Firebase Firestore). Precisávamos garantir que:
- Os dados fossem salvos localmente (Room) para acesso offline
- Os dados fossem sincronizados automaticamente com o Firestore quando online
- Ambos os bancos permanecessem "espelhados" sem conflitos
- O aplicativo funcionasse perfeitamente sem conexão à internet

**Soluções Implementadas:**

1. **Persistência Habilitada no Firestore:**
```kotlin
val settings = FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .build()
db.firestoreSettings = settings
```

2. **Estratégia de Fallback em Leituras:**
```kotlin
suspend fun getQuestionsByQuizId(quizId: String): List<Question> {
    return try {
        // Tenta buscar online
        val snapshot = db.collection("questions").whereEqualTo("quizId", quizId).get().await()
        snapshot.toObjects(Question::class.java).sortedBy { it.orderIndex }
    } catch (e: Exception) {
        try {
            // Fallback para cache local do Firestore
            val cachedSnapshot = db.collection("questions")
                .whereEqualTo("quizId", quizId)
                .get(Source.CACHE).await()
            cachedSnapshot.toObjects(Question::class.java).sortedBy { it.orderIndex }
        } catch (e2: Exception) {
            emptyList()
        }
    }
}
```

3. **Armazenamento Duplo com Room + SharedPreferences:**
- Room Database para dados estruturados (estatísticas, usuários)
- SharedPreferences para credenciais offline ("BypassOffline")
- Firestore para sincronização em nuvem

4. **Listeners em Tempo Real:**
```kotlin
dbRemote.collection("quizzes")
    .addSnapshotListener { quizSnapshot, _ ->
        // Atualiza automaticamente quando há mudanças
    }
```

**Resultado:**
O aplicativo agora funciona 100% offline após o primeiro login, com sincronização automática quando a conexão retorna.

---

### **Desafio 2️⃣: Garantir Consistência de Dados**

**Problema:**
Quando um usuário completa um quiz offline, como garantir que a pontuação seja salva corretamente e sincronizada depois?

**Solução:**
Implementamos salvamento duplo em todas as operações críticas:
```kotlin
// Salva localmente no Room
dbLocal.userDao().insertUser(userEntity)

// Salva remotamente no Firestore
quizRepository.saveQuizAttempt(uid, quizId, score)
```

### **Desafio 3️⃣: Versionamento de código e utilização do GitHub**
Foram identificadas dificuldades relacionadas ao gerenciamento do versionamento do código-fonte. A experiência limitada de parte da equipe com o GitHub exigiu alinhamento das práticas de desenvolvimento colaborativo.

### **Desafio 4️⃣: Experiência limitada com Kotlin e persistência de dados**

A equipe possui experiência reduzida com a linguagem Kotlin, o que demandou maior tempo de estudo, experimentação e validação das funcionalidades implementadas. Além disso, a adoção de dois bancos de dados para a persistência das informações aumentou a complexidade do projeto, exigindo maior atenção à configuração, integração e consistência dos dados.

---

## 👨‍💻 Autores
1. [Bruna Teodoro](https://github.com/BTeo08)
2. [Felipe Sérgio](https://github.com/lipesdf)
3. [Ricardo Ranzatti](https://github.com/Ranzatti)
4. [Tainá Peixoto](https://github.com/peixotots)
