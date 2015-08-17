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
    
    var statusBarItem: NSStatusItem = NSStatusItem()
    var menu: NSMenu = NSMenu()
    var toolTip: NSMenuItem = NSMenuItem()
    
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
        
        toolTip.title = "Checking with the bot..."
        toolTip.keyEquivalent = ""
        
        menu.addItem(toolTip)
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
            toolTip.title = "\(getTime()): No carafe present"
            return
        }
        
        if let cupsRemaining = coffeeProcessor.cupsRemaining(result!) {
            let clampedCupsRemaining = max(0, min(10, cupsRemaining))
            let lastBrewed = coffeeProcessor.lastBrewed(result!)
            
            updateButtonImage(coffeeCupImages[clampedCupsRemaining])
            
            if(clampedCupsRemaining == 1) {
                toolTip.title = "\(getTime()): 1 cup remaining, \(getBrewedTime(lastBrewed))"
            }
            else {
                toolTip.title = "\(getTime()): \(clampedCupsRemaining) cups remaining, \(getBrewedTime(lastBrewed))"
            }
        }
    }
    
    private func getTime() -> String {
        let date = NSDate()
        let calendar = NSCalendar.currentCalendar()
        let components = calendar.components(.CalendarUnitHour | .CalendarUnitMinute, fromDate: date)
        let hour = components.hour
        let minutes = components.minute
        
        return "\(hour):\(minutes)"
    }
    
    private func getBrewedTime(brewedTime: Int?) -> String {
        if (brewedTime == nil) {
            return "no brew time available"
        }
        
        if brewedTime < 0 {
            return "not sure when it was brewed"
        }
        
        let date = NSDate().timeIntervalSince1970
        let epoch = Int(round(date) * 1000)
        var difference = epoch - brewedTime!
        
        let days = difference / 86400000
        difference = difference % 86400000
        let hours = difference / 3600000
        difference = difference % 3600000
        let minutes = difference / 60000
        
        var result = "brewed "
        var separator = ""
        
        if days > 0 {
            result += "\(separator)\(days) day"
            
            if days != 1 {
            result += "s"
            }
            
            separator = ", "
        }
        
        if hours > 0 {
            result += "\(separator)\(hours) hour"
            
            if hours != 1 {
                result += "s"
            }
            
            separator = ", "
        }
        
        if minutes > 0 {
            result += "\(separator)\(minutes) minute"
            
            if minutes != 1 {
                result += "s"
            }
            
            separator = ", "
        }
        
        result += " ago"
        return result
    }
}