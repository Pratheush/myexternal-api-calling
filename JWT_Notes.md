### JWT Token
JWT is a way to securely transmit information between parties as a JSON object.
JWT is a compact, URL-safe token that can carry information between parties.
It uses Base64 encoding means that is URL-Safe for example if in url there is & and if we directly supply & then it would raise an issue so for safety before sending such values or characters or attributes are Base64 encoded for URL-Safety
A JWT is a string consisting of three parts, separated by dots
1. Header
2. Payload
3. Signature 

### Header
  The header typically consists of two parts: the type of the token(JWT) and the signing algorithm being used, such as HMAC SHA256 or RSA.
#### HEADER:ALGORITHM & TOKEN TYPE
```
{
  "alg": "HS256",
  "typ": "JWT"
}
```
### Payload
The payload contains the claims. Claims are statements about an entity (typically, the user) and additional metadata.
PAYLOAD DATA:
```
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}
```
### Signature
The signature is used to verify that the sender of the JWT is who it says it is and to ensure that the message wasn't changed along the way. To create the signature part, you have to take the encoded header, the encoded payload, a secret, the algorithm specified in the header, and sign that.

VERIFY SIGNATURE
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  your-256-bit-secret

) secret base64 encoded
```

Confirm if the API is returning a JWT in the expected format (header.payload.signature).
































































































































































































































