# Contributing to SylvenOS

Thank you for your interest in contributing to SylvenOS.

Our mission is simple: **Learn. Build. Educate.**

We welcome contributions in code, design, documentation, testing, and community support.

By contributing, you agree to follow our [Code of Conduct](./CODE_OF_CONDUCT.md).

## Getting Started

1. Fork and clone the repository.
2. Install dependencies:

   ```bash
   npm install
   cp .env.example .env.local
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

4. Create a descriptive branch:

   ```bash
   git checkout -b feat/short-description
   ```

## Making Changes

- Keep pull requests focused and small.
- Follow the existing code style.
- Ensure responsive design and light/dark theme compatibility.
- Reuse existing types and avoid `any`.
- Test your changes before submitting.

## Before Opening a PR

Run the following checks:

```bash
npm run lint
npm run type-check
npm test
npm run build
```

All checks should pass before submitting.

## Commit Messages

Follow Conventional Commits:

```text
feat: add new feature
fix: resolve a bug
docs: update documentation
chore: maintenance tasks
```

## Pull Requests

- Submit your PR against `main`.
- Use the provided PR template.
- Explain your changes clearly.
- Include screenshots for UI changes.
- Link related issues using `Closes #123`.
- Address review feedback.

## Bugs & Feature Requests

Report bugs and suggest features through [GitHub Issues](https://github.com/sylvenos/sylvenos-web/issues).

Include reproduction steps, expected behavior, actual behavior, and relevant screenshots.

## Security Issues

Do not report security vulnerabilities publicly. Follow the instructions in [SECURITY.md](./SECURITY.md).

## Questions & Support

Join our [Discord community](https://discord.com/invite/HNrEcrSBs6) or start a GitHub Discussion.

Thank you for helping build SylvenOS. 🚀