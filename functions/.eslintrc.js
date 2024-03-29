module.exports = {
  "root": true,
  "env": {
    es6: true,
    node: true,
  },
  "extends": [
    "eslint:recommended",
    "google",
    "parser:babel-eslint",
  ],
  // "parser": "babel-eslint",
  "rules": {
    quotes: ["error", "double"],
  },
  "parserOptions": {
    "ecmaVersion": 8,
  },
};
