# auth-by-env

![Build](https://github.com/intfish123/auth-by-env/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/22397.svg)](https://plugins.jetbrains.com/plugin/22397)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22397.svg)](https://plugins.jetbrains.com/plugin/22397)

<!-- Plugin description -->
Get `user` and `password` from `environment variables`, then connecting Datasource.

## Getting Start
### Step 1: Config your environment variables

Examples:
```
echo "export DB_USERNAME=your-username" >> ~/.zshrc
echo "export DB_PASSWORD=your-password" >> ~/.zshrc
```

### Step 2: Install this plugin and config datasource
switch `Authentication:` to `Auth By Env`


<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "auth-by-env"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/intfish123/auth-by-env/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
