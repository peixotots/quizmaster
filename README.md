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
- [Roadmap de Desenvolvimento](#roadmap-de-desenvolvimento)
- [Screenshots](#screenshots)

---

## 🎯 Sobre o Projeto

O **Quiz Master** é um aplicativo Android desenvolvido como projeto acadêmico que implementa um sistema completo de quizzes com autenticação individual, armazenamento local e sincronização em nuvem. O aplicativo permite que usuários respondam quizzes de diferentes temas, acompanhem seu desempenho e visualizem estatísticas detalhadas.

### **Objetivo Acadêmico**
Demonstrar competências em:
- Desenvolvimento mobile Android nativo com Kotlin
- Integração com Firebase (Authentication & Firestore)
- Persistência de dados local com Room Database
- Interface moderna com Jetpack Compose
- Arquitetura MVVM e Clean Architecture

---

## ✨ Funcionalidades

### **Módulo 1: Autenticação**

#### 🔐 **Sistema de Autenticação**
- **Cadastro de Usuário**
  - Coleta de nome completo, e-mail e senha
  - Validação em tempo real dos campos
  - Confirmação de senha
  - Persistência automática no Firebase Firestore
  
- **Login Seguro**
  - Autenticação via Firebase Authentication
  - Tratamento de erros amigável
  - Alternância de visibilidade de senha
  
- **Recuperação de Acesso**
  - Sistema "Esqueci minha senha"
  - Envio automático de e-mail de redefinição
  
- **Perfil do Usuário**
  - Armazenamento no Firestore com:
    - Nome completo
    - E-mail
    - Pontuação (score)
    - Data de criação (createdAt)

### **Módulo 2: Gestão de Questões**

#### 📥 **Download e Armazenamento Local**
- Download de questões do Firebase Realtime Database/Firestore
- Armazenamento local com Room Database para uso offline
- Sincronização automática quando houver atualizações
- Cache inteligente de dados

### **Módulo 3: Execução de Quiz**

#### 🎮 **Sistema de Quiz Dinâmico**
- Apresentação de questões armazenadas localmente
- Interface interativa para seleção de respostas
- Cronômetro para medir tempo de resposta
- Cálculo automático de pontuação

#### 📊 **Exibição de Desempenho**
- Percentual de acertos
- Tempo total gasto
- Pontuação final
- Salvamento de sessão (local + nuvem)

### **Módulo 4: Histórico e Estatísticas**

#### 📈 **Acompanhamento de Progresso**
- Histórico completo de quizzes respondidos
- Estatísticas detalhadas:
  - Total de quizzes realizados
  - Taxa de acerto geral
  - Tempo médio por quiz
  - Evolução de desempenho
- Comparação entre sessões

### **Módulo 5: Interface e Experiência**

#### 🎨 **Design Premium**
- Layout personalizado
- Interface responsiva

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
| **Firebase Realtime Database** | Sincronização em tempo real |

### **Persistência Local**
| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Room Database** | 2.8.4 | ORM para SQLite |
| **Room KTX** | 2.8.4 | Extensões Kotlin para Room |

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
    │   │   └── QuizDatabase.kt   # Configuração do Room
    │   ├── remote/                # Firebase
    │   │   ├── FirebaseAuth.kt
    │   │   └── FirestoreRepository.kt
    │   └── repository/            # Repositórios (Single Source of Truth)
    │
    ├── domain/                    # Camada de Domínio
    │   ├── models/               # Modelos de domínio
    │   └── usecases/             # Casos de uso
    │
    ├── ui/                        # Camada de Apresentação
    │   ├── screens/              # Telas Compose
    │   │   ├── auth/             # Login, Cadastro, Recuperação
    │   │   ├── home/             # Dashboard principal
    │   │   ├── quiz/             # Execução de quiz
    │   │   └── history/          # Histórico e estatísticas
    │   ├── components/           # Componentes reutilizáveis
    │   ├── theme/                # Tema e estilos
    │   └── viewmodels/           # ViewModels
    │
    ├── utils/                     # Utilitários
    │   └── FirebaseErrorHelper.kt
    │
    └── MainActivity.kt            # Activity principal
```

### **Fluxo de Dados**
```
UI (Compose) → ViewModel → Repository → Data Source (Room/Firebase)
                   ↓
              State/Flow
                   ↓
            UI Atualização
```
---

## 📁 Estrutura de Pastas

```
quizmaster/
├── .idea/                    # Configurações do Android Studio
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/quizandroid/
│   │   │   │   ├── data/           # Repositórios e fontes de dados
│   │   │   │   ├── ui/             # Interface do usuário
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── FirebaseErrorHelper.kt
│   │   │   ├── res/                # Recursos (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                   # Testes unitários
│   ├── build.gradle.kts            # Dependências do módulo app
│   └── google-services.json        # Configuração Firebase
├── gradle/                         # Wrapper do Gradle
├── build.gradle.kts                # Configuração Gradle raiz
├── settings.gradle.kts             # Configuração de módulos
├── gradle.properties               # Propriedades do projeto
└── README.md                       # Este arquivo
```
---

## 👨‍💻 Autores
1. [Bruna Teodoro](https://github.com/BTeo08)
2. [Felipe Sérgio](https://github.com/lipesdf)
3. [Ricardo Ranzatti](https://github.com/Ranzatti)
4. [Tainá Peixoto](https://github.com/peixotots)
