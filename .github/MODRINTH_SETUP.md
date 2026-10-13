# Modrinth Auto-Deploy Setup Guide

This guide explains how to configure automatic deployment from GitHub Actions to Modrinth.

## 📋 Required Configuration

### 1. Create a Modrinth Project

1. Create an account on [Modrinth](https://modrinth.com)
2. Create a new project
3. Note the project ID (for example, `best-auction`)

### 2. Obtain a Modrinth API Token

1. Visit [Modrinth Settings](https://modrinth.com/settings/pats)
2. Click “Create a PAT”
3. Grant the following permissions:
   - `CREATE_VERSION` - Create new versions
   - `EDIT_DETAILS` - Edit project details
4. Copy the generated token

### 3. Configure GitHub Secrets

Add the following under Settings > Secrets and variables > Actions in your GitHub repository:

```
MODRINTH_PAT=<your_Modrinth_token>          # ✅ Already configured
MODRINTH_PROJECT_ID=<your_project_ID>      # 📝 Configuration required
```

**Note**: `MODRINTH_PAT` is already configured. Only `MODRINTH_PROJECT_ID` needs to be set.

### 4. Adjust the Workflow File

Adjust the following section of `.github/workflows/release.yml`:

```yaml
# Modrinth configuration
modrinth-id: ${{ secrets.MODRINTH_PROJECT_ID }}  # Your project ID
modrinth-token: ${{ secrets.MODRINTH_TOKEN }}

# Game versions (adjust the supported versions)
game-versions: |
  1.20.6
  1.20.5
  1.20.4
  1.20.1

# Mod loaders (adjust the supported platforms)
loaders: |
  paper
  spigot
  bukkit
```

## 🚀 Deployment Steps

### Automatic Deployment
Deployment runs automatically when you push a v tag:

```bash
# Example: Release version 1.0.0
git tag v1.0.0
git push origin v1.0.0
```

### Manual Checklist

Before deploying, check the following:

- [ ] The version in `build.gradle.kts` is correct
- [ ] The information in `plugin.yml` is up to date
- [ ] Documentation is updated
- [ ] Tests pass (`./gradlew test`)

## 📝 Modrinth Project Settings

### Project Information
- **Name**: Best Auction
- **Description**: Complete auction house plugin for Minecraft servers
- **Categories**: Economy, Utility
- **License**: MIT
- **Issues URL**: `https://github.com/0x48lab/best_auction/issues`
- **Source URL**: `https://github.com/0x48lab/best_auction`
- **Wiki URL**: `https://github.com/0x48lab/best_auction/blob/main/PLUGIN_DOCUMENTATION.md`

### Gallery Images
Recommended screenshots:
- Main auction house GUI
- Bidding screen
- Configuration file example

## 🔧 Troubleshooting

### Common Problems

**1. Deployment Fails**
- Check that the Modrinth API token is configured correctly
- Check that the project ID is correct
- Check that permissions are configured appropriately

**2. JAR File Not Found**
- Check that the Gradle build succeeded
- Check that the file path is correct (`build/libs/`)

**3. Duplicate Version**
- Check that the same version number has not been deployed more than once
- Check that the tag is set correctly

### Checking Logs

You can find error details in the GitHub Actions execution logs:
1. Open the “Actions” tab in the GitHub repository
2. Click the failed workflow
3. Check the logs for the “deploy” job

## 📊 Metrics

After deployment, check the following metrics:
- Download count
- Project follower count
- Usage by version

You can find this information in the Modrinth project dashboard.

## 🔄 Update Cycle

Recommended update cycle:
- **Patch release** (v1.0.1): Bug fixes
- **Minor release** (v1.1.0): New features
- **Major release** (v2.0.0): Breaking changes

Provide an appropriate changelog with each release so users clearly understand the changes.