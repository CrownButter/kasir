# 1. Build Stage (Node.js)
FROM node:20-alpine AS build
WORKDIR /app

# --- WAJIB: Terima variabel dari docker-compose agar terbaca oleh Vite ---
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
# -----------------------------------------------------------------------

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 2. Production Stage (Nginx)
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Pastikan file nginx.conf kamu ada di folder yang sama dengan Dockerfile ini
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]