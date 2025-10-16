/** @type {import('tailwindcss').Config} */

const defaultTheme = require("tailwindcss/defaultTheme");

module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      screens: {
        md: "860px",
      },
      fontFamily: {
        sans: ["Roboto", defaultTheme.fontFamily.sans],
      },
      colors: {
        sky: {
          ...defaultTheme.colors.sky,
          700: "rgb(0, 69, 103)",
        },
      },
    },
  },
  plugins: [require("daisyui")],
};
