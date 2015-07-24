//
//  AppDelegate.swift
//  CoffeeBar
//
//  Created by Tim Mattison on 7/24/15.
//  Copyright (c) 2015 controlgroup. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
    let configPlistName = "Config"
    let apiEndpointConfigKeyName = "API endpoint"
    let refreshIntervalInSeconds = 15.0
    
    let coffeeProcessor = CoffeeProcessor()
    
    let statusItem = NSStatusBar.systemStatusBar().statusItemWithLength(-2)
    var refreshTimer:NSTimer?
    var endpointUrl:String?
    var coffeeCupImage:NSImage?
    var noCarafeImage:NSImage?
    
    var coffeeCupImages = [NSImage]()
    
    func applicationDidFinishLaunching(aNotification: NSNotification) {
        loadButtonIcons()
        createMenu()
        populateEndpointUrl()
        createRefreshTimer()
    }
    
    private func loadButtonIcons() {
        coffeeCupImage = NSImage(named: "StatusBarButtonImage")!
        noCarafeImage = NSImage(named: "NoCarafe")
        
        coffeeCupImages.append(coffeeCupImage!)
        coffeeCupImages.append(NSImage(named: "1Cup")!)
        coffeeCupImages.append(NSImage(named: "2Cups")!)
        coffeeCupImages.append(NSImage(named: "3Cups")!)
        coffeeCupImages.append(NSImage(named: "4Cups")!)
        coffeeCupImages.append(NSImage(named: "5Cups")!)
        coffeeCupImages.append(NSImage(named: "6Cups")!)
        coffeeCupImages.append(NSImage(named: "7Cups")!)
        coffeeCupImages.append(NSImage(named: "8Cups")!)
        coffeeCupImages.append(NSImage(named: "9Cups")!)
        coffeeCupImages.append(NSImage(named: "10Cups")!)
        
        updateButtonImage(coffeeCupImage)
    }
    
    private func updateButtonImage(input: NSImage!) {
        dispatch_async(dispatch_get_main_queue()) {
            if let button = self.statusItem.button {
                button.image = input
            }
        }
    }
    
    private func createMenu() {
        let menu = NSMenu()
        
        menu.addItem(NSMenuItem(title: "Quit CoffeeBar", action: Selector("terminate:"), keyEquivalent: "q"))
        
        statusItem.menu = menu
    }
    
    private func populateEndpointUrl() {
        if let path = NSBundle.mainBundle().pathForResource(configPlistName, ofType: "plist") {
            if let dict = NSDictionary(contentsOfFile: path) as? Dictionary<String, AnyObject> {
                // TODO: Check if this is "CHANGEME" and remind the user to change it
                endpointUrl = dict[apiEndpointConfigKeyName] as? String
            }
        }
    }
    
    private func createRefreshTimer() {
        refreshTimer = NSTimer(timeInterval: refreshIntervalInSeconds, target: self, selector: "refreshTimerFired", userInfo: nil, repeats: true)
        
        if refreshTimer != nil {
            NSRunLoop.currentRunLoop().addTimer(refreshTimer!, forMode: NSRunLoopCommonModes)
        }
    }
    
    func refreshTimerFired() {
        let result: AnyObject? = coffeeProcessor.pollCoffeeUrl(endpointUrl)
        
        if result == nil {
            // TODO: Eventually let the user know that the coffee status is invalid
            return
        }
        
        let carafePresent = coffeeProcessor.carafePresent(result!)
        
        if carafePresent == false {
            updateButtonImage(noCarafeImage)
            return
        }
        
        if let cupsRemaining = coffeeProcessor.cupsRemaining(result!) {
            let clampedCupsRemaining = max(0, min(10, cupsRemaining))
            
            updateButtonImage(coffeeCupImages[clampedCupsRemaining])
        }
    }
}

