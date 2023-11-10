# Base Package
FROM node:18-alpine

# Create app directory
WORKDIR /app/

EXPOSE 3000

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY public/ /app/public
COPY src/ /app/src
COPY package.json /app/

RUN npm install

# Bundle app source
COPY . .

#RUN yarn install

# Initiate npm start
CMD [ "npm", "start" ]