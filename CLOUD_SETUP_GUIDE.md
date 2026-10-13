# Best Auction Cloud Integration Setup Guide

This guide explains how to configure cloud integration for the Best Auction plugin.

## 📋 Prerequisites

- The Best Auction plugin is installed
- The server can connect to the internet
- A Cloudflare Workers account (the free plan is sufficient)

## 🚀 Setup Steps

### Step 1: Set Up Cloudflare Workers

1. Visit [Cloudflare](https://www.cloudflare.com/) and create an account
2. Select “Workers & Pages” from the dashboard
3. Create a new Worker
4. Deploy the Worker code for Best Auction (available [here](https://github.com/0x48lab/best-auction-cloud-worker))

### Step 2: Obtain an API Token

#### Method 1: Use Console Commands (Recommended)

1. Run the following in your Minecraft server console:
   ```
   ah cloud gettoken
   ```

2. Follow the displayed URL and instructions to obtain a token

3. Set the token in the console:
   ```
   ah cloud settoken YOUR_API_TOKEN_HERE
   ```

#### Method 2: Edit the Configuration File Manually

1. Stop the server
2. Open `plugins/best_auction/config.yml`
3. Edit the settings below

### Step 3: Configure config.yml

```yaml
# Cloud integration settings
cloud:
  # Enable cloud integration
  enabled: true
  
  # Cloudflare Workers base URL
  # Example: https://best-auction.your-username.workers.dev
  base_url: "https://your-worker.your-subdomain.workers.dev"
  
  # API token (obtained in Step 2)
  api_token: "your-api-token-here"
  
  # Server identifier (use a different value for each server if you have multiple servers)
  server_id: "server-1"
  
  # Authentication settings
  auth:
    # Automatically validate the token on startup
    validate_on_startup: true
    
    # Token revalidation interval (minutes)
    revalidate_interval: 60
  
  # Data synchronization settings
  sync:
    # Automatically synchronize existing data on the first startup
    auto_sync_on_startup: true
    
    # Batch size (number of auctions sent at once)
    batch_size: 50
    
    # Delay between batches (milliseconds)
    batch_delay: 1000
    
    # Synchronize bid information as well
    include_bids: true
  
  # HTTP request timeout (milliseconds)
  timeout: 5000
  
  # Retry settings
  retry_attempts: 3
  retry_delay: 1000
  
  # Enable or disable individual event types
  events:
    item_listed: true      # When an item is listed
    bid_placed: true       # When a bid is placed
    bid_cancelled: true    # When a bid is cancelled
    auction_cancelled: true # When an auction is cancelled
    item_sold: true        # When an item is sold
```

### Step 4: Verify the Configuration

1. Start the server

2. Check the integration status with the following command:
   ```
   /ah cloud status
   ```

3. Validate the token:
   ```
   /ah cloud validate
   ```

### Step 5: Initial Data Synchronization (Optional)

To synchronize existing auction data to the cloud:

```
/ah cloud sync
```

To force a full resynchronization:

```
/ah cloud sync force
```

## 🔍 Verify Operation

1. Try listing an item:
   ```
   /ah sell 1000
   ```

2. Check the server log:
   - If `[CloudEvent] Event sent successfully: ITEM_LISTED` appears, the event was sent successfully

3. Check the requests in the Cloudflare Workers logs

## ❌ Troubleshooting

### Error: "Cloud integration is disabled in config"
→ Set `cloud.enabled: true` in `config.yml`

### Error: "Cloud base URL not configured"
→ Set `cloud.base_url` to your Worker's URL

### Error: "Cloud API token not configured"
→ Set your token in `cloud.api_token`

### Error: "Cloud API token is not valid"
→ The token is invalid. Obtain and configure a new token

### Error: "timestamp: Invalid datetime format"
→ Update the plugin to the latest version (the timestamp format has been fixed)

### Events Are Not Being Sent
1. Check the status with `/ah cloud status`
2. Check that the relevant event is set to true in the `cloud.events` section
3. Check the server log for `[CloudEvent]` messages

## 📊 Checking Logs

To display detailed logs, configure the following in `server.properties`:

```properties
# Set the log level to FINE (for debugging)
java.util.logging.ConsoleHandler.level=FINE
```

Alternatively, check any log files generated in `plugins/best_auction/`.

## 🔒 Security Notes

- API tokens are secret. Do not publish them on GitHub or elsewhere
- If a token is leaked, generate a new one in Cloudflare Workers
- Data is encrypted using HTTPS

## 📞 Support

If the problem persists:
1. Report it on [GitHub Issues](https://github.com/0x48lab/best_auction/issues)
2. Include detailed error logs