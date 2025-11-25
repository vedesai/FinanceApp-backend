# How to Access Your Finance App After Deployment

Once your service is running on EC2, follow these steps to access it:

## Step 1: Get Your EC2 Instance Public IP/DNS

1. Go to **AWS Console → EC2 → Instances**
2. Select your EC2 instance
3. Note the **Public IPv4 address** (e.g., `54.123.45.67`)
   - Or use **Public IPv4 DNS** (e.g., `ec2-3-233-226-50.compute-1.amazonaws.com`)

## Step 2: Configure Security Group

Your EC2 security group must allow HTTP traffic on port 8080:

1. Go to **EC2 → Security Groups → Select your instance's security group**
2. Click **Edit Inbound Rules**
3. Add a new rule:
   - **Type**: Custom TCP
   - **Protocol**: TCP
   - **Port**: 8080
   - **Source**: 
     - For testing: `0.0.0.0/0` (allows from anywhere)
     - For production: Your frontend domain IP or specific IPs
   - **Description**: "Allow Finance App API access"
4. Click **Save rules**

## Step 3: Verify Service is Running

SSH into your EC2 instance and verify:

```bash
# Check service status
sudo systemctl status finance-app

# Check if port 8080 is listening
sudo netstat -tlnp | grep :8080

# View logs
sudo journalctl -u finance-app -f
```

## Step 4: Test API Endpoints

### From Your Local Machine

```bash
# Replace with your EC2 public IP
export EC2_IP="54.123.45.67"

# Test health/availability
curl http://$EC2_IP:8080/api/dashboard

# Test investments endpoint
curl http://$EC2_IP:8080/api/investments

# Test assets endpoint
curl http://$EC2_IP:8080/api/assets

# Test liabilities endpoint
curl http://$EC2_IP:8080/api/liabilities
```

### From Browser

Open in your browser:
```
http://YOUR_EC2_PUBLIC_IP:8080/api/dashboard
```

Example:
```
http://54.123.45.67:8080/api/dashboard
```

## Step 5: Update CORS Configuration (For Frontend Access)

If your frontend is hosted elsewhere, update CORS settings:

### Option A: Update Environment File on EC2

SSH into EC2 and edit `/opt/finance-app/.env`:

```bash
sudo nano /opt/finance-app/.env
```

Add or update:
```bash
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001,https://your-frontend-domain.com
```

Restart the service:
```bash
sudo systemctl restart finance-app
```

### Option B: Update GitHub Secret

1. Go to **GitHub → Repository → Settings → Secrets**
2. Add or update `ALLOWED_ORIGINS` secret:
   ```
   http://localhost:3000,http://localhost:3001,https://your-frontend-domain.com
   ```
3. Redeploy the application

## Step 6: Update Frontend API Configuration

Update your frontend to point to the EC2 instance:

### If using environment variables:

```javascript
// .env or config file
REACT_APP_API_URL=http://YOUR_EC2_PUBLIC_IP:8080
```

### If hardcoded in api.js:

```javascript
// frontend/src/services/api.js
const API_BASE_URL = 'http://YOUR_EC2_PUBLIC_IP:8080/api';
```

## Step 7: Access from Frontend

Once configured:

1. Start your frontend locally or deploy it
2. The frontend will call the API at `http://YOUR_EC2_PUBLIC_IP:8080/api`
3. Ensure CORS is configured to allow your frontend origin

## API Endpoints Available

Once running, these endpoints are available:

### Dashboard
- `GET http://YOUR_EC2_IP:8080/api/dashboard`

### Investments
- `GET http://YOUR_EC2_IP:8080/api/investments`
- `GET http://YOUR_EC2_IP:8080/api/investments/{id}`
- `POST http://YOUR_EC2_IP:8080/api/investments`
- `PUT http://YOUR_EC2_IP:8080/api/investments/{id}`
- `DELETE http://YOUR_EC2_IP:8080/api/investments/{id}`

### Assets
- `GET http://YOUR_EC2_IP:8080/api/assets`
- `GET http://YOUR_EC2_IP:8080/api/assets/{id}`
- `POST http://YOUR_EC2_IP:8080/api/assets`
- `PUT http://YOUR_EC2_IP:8080/api/assets/{id}`
- `DELETE http://YOUR_EC2_IP:8080/api/assets/{id}`

### Liabilities
- `GET http://YOUR_EC2_IP:8080/api/liabilities`
- `GET http://YOUR_EC2_IP:8080/api/liabilities/{id}`
- `POST http://YOUR_EC2_IP:8080/api/liabilities`
- `PUT http://YOUR_EC2_IP:8080/api/liabilities/{id}`
- `DELETE http://YOUR_EC2_IP:8080/api/liabilities/{id}`

### ICICIDirect Sync
- `POST http://YOUR_EC2_IP:8080/api/icicidirect/sync`

## Troubleshooting Access Issues

### Issue: Connection Refused

**Check:**
1. Service is running: `sudo systemctl status finance-app`
2. Port 8080 is listening: `sudo netstat -tlnp | grep :8080`
3. Security group allows port 8080
4. EC2 instance is running

**Solution:**
```bash
# Restart service
sudo systemctl restart finance-app

# Check logs
sudo journalctl -u finance-app -n 50
```

### Issue: CORS Error in Browser

**Check:**
- CORS configuration includes your frontend origin
- Frontend is calling the correct API URL

**Solution:**
Update `ALLOWED_ORIGINS` in `/opt/finance-app/.env` and restart:
```bash
sudo systemctl restart finance-app
```

### Issue: 404 Not Found

**Check:**
- API endpoint path is correct (`/api/...`)
- Application started successfully

**Solution:**
```bash
# Check service logs
sudo journalctl -u finance-app -n 50

# Test locally on EC2
curl http://localhost:8080/api/dashboard
```

### Issue: 500 Internal Server Error

**Check:**
- Database connection is working
- Environment variables are set correctly
- Check application logs

**Solution:**
```bash
# View detailed logs
sudo journalctl -u finance-app -n 100

# Check database connectivity
# (from EC2 instance)
psql -h YOUR_RDS_ENDPOINT -U postgres -d financedb
```

## Using Custom Domain (Optional)

For production, you can set up a custom domain:

1. **Get a domain** (e.g., from Route 53 or other registrar)
2. **Set up DNS** pointing to your EC2 public IP
3. **Configure SSL** using Let's Encrypt or AWS Certificate Manager
4. **Set up reverse proxy** (nginx) to forward requests to port 8080
5. **Update CORS** to allow your custom domain

## Quick Test Script

Run this to test all endpoints:

```bash
#!/bin/bash
EC2_IP="YOUR_EC2_PUBLIC_IP"

echo "Testing Dashboard..."
curl -s http://$EC2_IP:8080/api/dashboard | jq . || echo "Failed"

echo "Testing Investments..."
curl -s http://$EC2_IP:8080/api/investments | jq . || echo "Failed"

echo "Testing Assets..."
curl -s http://$EC2_IP:8080/api/assets | jq . || echo "Failed"

echo "Testing Liabilities..."
curl -s http://$EC2_IP:8080/api/liabilities | jq . || echo "Failed"
```

## Security Recommendations

1. **Restrict Security Group**: Don't allow `0.0.0.0/0` in production
2. **Use HTTPS**: Set up SSL/TLS certificate
3. **Use Load Balancer**: Consider Application Load Balancer for production
4. **Monitor Access**: Set up CloudWatch alarms
5. **Regular Updates**: Keep EC2 instance and Java updated

## Next Steps

- Set up monitoring and alerts
- Configure automated backups
- Set up CI/CD for frontend deployment
- Consider using Application Load Balancer
- Set up CloudWatch logging
- Configure auto-scaling if needed

