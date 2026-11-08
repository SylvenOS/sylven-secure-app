name: Security Audit

on:
  workflow_call:

  schedule:
    - cron: '0 6 * * 1'

  push:
    branches:
      - main

  pull_request:
    branches:
      - main

jobs:
  dependency-review:
    if: github.event_name == 'pull_request'
    name: Dependency Review
    runs-on: ubuntu-latest

    permissions:
      contents: read
      pull-requests: read

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Dependency Review
        uses: actions/dependency-review-action@v4

  gradle-dependency-submission:
    name: Gradle Dependency Submission
    runs-on: ubuntu-latest

    permissions:
      contents: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Setup JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Submit Gradle dependency graph
        uses: gradle/actions/dependency-submission@v4
